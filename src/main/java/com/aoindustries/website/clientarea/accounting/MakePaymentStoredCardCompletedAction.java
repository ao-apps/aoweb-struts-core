/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoweb-struts-core.
 *
 * aoweb-struts-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoweb-struts-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoweb-struts-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.TransactionType;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.creditcards.AOServConnectorPrincipal;
import com.aoindustries.aoserv.creditcards.BusinessGroup;
import com.aoindustries.aoserv.creditcards.CreditCardFactory;
import com.aoindustries.aoserv.creditcards.CreditCardProcessorFactory;
import com.aoindustries.creditcards.AuthorizationResult;
import com.aoindustries.creditcards.CaptureResult;
import com.aoindustries.creditcards.CreditCardProcessor;
import com.aoindustries.creditcards.Transaction;
import com.aoindustries.creditcards.TransactionRequest;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

/**
 * Payment from stored credit card.
 *
 * @author  AO Industries, Inc.
 */
public class MakePaymentStoredCardCompletedAction extends MakePaymentStoredCardAction {

	@Override
	final public ActionForward executePermissionGranted(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		MakePaymentStoredCardForm makePaymentStoredCardForm = (MakePaymentStoredCardForm)form;

		// Init request values
		String accounting = makePaymentStoredCardForm.getAccounting();
		Account account = accounting==null ? null : aoConn.getAccount().getAccount().get(Account.Name.valueOf(accounting));
		if(account == null) {
			// Redirect back to make-payment if business not found
			return mapping.findForward("make-payment");
		}

		// If the card pkey in "", new card was selected
		String pkeyString = makePaymentStoredCardForm.getPkey();
		if(pkeyString==null) {
			// pkey not provided, redirect back to make-payment
			return mapping.findForward("make-payment");
		}
		if("".equals(pkeyString)) {
			response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-new-card.do?accounting="+request.getParameter("accounting")));
			return null;
		}

		int id;
		try {
			id = Integer.parseInt(pkeyString);
		} catch(NumberFormatException err) {
			// Can't parse int, redirect back to make-payment
			return mapping.findForward("make-payment");
		}
		CreditCard creditCard = aoConn.getPayment().getCreditCard().get(id);
		if(creditCard == null) {
			// creditCard not found, redirect back to make-payment
			return mapping.findForward("make-payment");
		}

		request.setAttribute("business", account);
		request.setAttribute("creditCard", creditCard);

		// Validation
		ActionMessages errors = makePaymentStoredCardForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);

			return mapping.findForward("input");
		}

		// Convert to pennies
		int pennies = SQLUtility.getPennies(makePaymentStoredCardForm.getPaymentAmount());
		BigDecimal paymentAmount = new BigDecimal(makePaymentStoredCardForm.getPaymentAmount());

		// Perform the transaction
		AOServConnector rootConn = siteSettings.getRootAOServConnector();

		// 1) Pick a processor
		CreditCard rootCreditCard = rootConn.getPayment().getCreditCard().get(creditCard.getPkey());
		if(rootCreditCard == null) throw new SQLException("Unable to find CreditCard: " + creditCard.getPkey());
		com.aoindustries.aoserv.client.payment.Processor rootAoProcessor = rootCreditCard.getCreditCardProcessor();
		CreditCardProcessor rootProcessor = CreditCardProcessorFactory.getCreditCardProcessor(rootAoProcessor);

		// 2) Add the transaction as pending on this processor
		Account rootAccount = rootConn.getAccount().getAccount().get(Account.Name.valueOf(accounting));
		if(rootAccount == null) throw new SQLException("Unable to find Account: " + accounting);
		TransactionType paymentTransactionType = rootConn.getBilling().getTransactionType().get(TransactionType.PAYMENT);
		if(paymentTransactionType == null) throw new SQLException("Unable to find TransactionType: " + TransactionType.PAYMENT);
		MessageResources applicationResources = (MessageResources)request.getAttribute("/clientarea/accounting/ApplicationResources");
		String paymentTypeName;
		String cardInfo = creditCard.getCardInfo();
		if(cardInfo.startsWith("34") || cardInfo.startsWith("37")) {
			paymentTypeName = PaymentType.AMEX;
		} else if(cardInfo.startsWith("60")) {
			paymentTypeName = PaymentType.DISCOVER;
		} else if(cardInfo.startsWith("51") || cardInfo.startsWith("52") || cardInfo.startsWith("53") || cardInfo.startsWith("54") || cardInfo.startsWith("55")) {
			paymentTypeName = PaymentType.MASTERCARD;
		} else if(cardInfo.startsWith("4")) {
			paymentTypeName = PaymentType.VISA;
		} else {
			paymentTypeName = null;
		}
		PaymentType paymentType;
		if(paymentTypeName==null) paymentType = null;
		else {
			paymentType = rootConn.getPayment().getPaymentType().get(paymentTypeName);
			if(paymentType == null) throw new SQLException("Unable to find PaymentType: " + paymentTypeName);
		}

		int transID = rootAccount.addTransaction(
			rootAccount,
			aoConn.getThisBusinessAdministrator(),
			paymentTransactionType,
			applicationResources.getMessage(locale, "makePaymentStoredCardCompleted.transaction.description"),
			1000,
			-pennies,
			paymentType,
			cardInfo,
			rootAoProcessor,
			com.aoindustries.aoserv.client.billing.Transaction.WAITING_CONFIRMATION
		);
		com.aoindustries.aoserv.client.billing.Transaction aoTransaction = rootConn.getBilling().getTransaction().get(transID);
		if(aoTransaction == null) throw new SQLException("Unable to find Transaction: " + transID);

		// 3) Process
		AOServConnectorPrincipal principal = new AOServConnectorPrincipal(rootConn, aoConn.getThisBusinessAdministrator().getUsername().getUsername().toString());
		BusinessGroup businessGroup = new BusinessGroup(rootAccount, accounting);
		Transaction transaction;
		if(MakePaymentNewCardCompletedAction.DEBUG_AUTHORIZE_THEN_CAPTURE) {
			transaction = rootProcessor.authorize(
				principal,
				businessGroup,
				new TransactionRequest(
					false, // testMode
					request.getRemoteAddr(), // customerIp
					MakePaymentNewCardCompletedAction.DUPLICATE_WINDOW,
					Integer.toString(transID), // orderNumber
					MakePaymentNewCardCompletedAction.USD, // currency
					paymentAmount, // amount
					null, // taxAmount
					false, // taxExempt
					null, // shippingAmount
					null, // dutyAmount
					null, // shippingFirstName
					null, // shippingLastName
					null, // shippingCompanyName
					null, // shippingStreetAddress1
					null, // shippingStreetAddress2
					null, // shippingCity
					null, // shippingState
					null, // shippingPostalCode
					null, // shippingCountryCode
					false, // emailCustomer
					null, // merchantEmail
					null, // invoiceNumber
					null, // purchaseOrderNumber
					applicationResources.getMessage(Locale.US, "makePaymentStoredCardCompleted.transaction.description") // description
				),
				CreditCardFactory.getCreditCard(rootCreditCard)
			);
		} else {
			transaction = rootProcessor.sale(
				principal,
				businessGroup,
				new TransactionRequest(
					false, // testMode
					request.getRemoteAddr(), // customerIp
					MakePaymentNewCardCompletedAction.DUPLICATE_WINDOW,
					Integer.toString(transID), // orderNumber
					MakePaymentNewCardCompletedAction.USD, // currency
					paymentAmount, // amount
					null, // taxAmount
					false, // taxExempt
					null, // shippingAmount
					null, // dutyAmount
					null, // shippingFirstName
					null, // shippingLastName
					null, // shippingCompanyName
					null, // shippingStreetAddress1
					null, // shippingStreetAddress2
					null, // shippingCity
					null, // shippingState
					null, // shippingPostalCode
					null, // shippingCountryCode
					false, // emailCustomer
					null, // merchantEmail
					null, // invoiceNumber
					null, // purchaseOrderNumber
					applicationResources.getMessage(Locale.US, "makePaymentStoredCardCompleted.transaction.description") // description
				),
				CreditCardFactory.getCreditCard(rootCreditCard)
			);
		}

		// 4) Decline/approve based on results
		AuthorizationResult authorizationResult = transaction.getAuthorizationResult();
		switch(authorizationResult.getCommunicationResult()) {
			case LOCAL_ERROR :
			case IO_ERROR :
			case GATEWAY_ERROR :
			{
				// Update transaction as failed
				aoTransaction.declined(Integer.parseInt(transaction.getPersistenceUniqueId()));
				// Get the list of active credit cards stored for this business
				List<CreditCard> allCreditCards = account.getCreditCards();
				List<CreditCard> creditCards = new ArrayList<>(allCreditCards.size());
				for(CreditCard tCreditCard : allCreditCards) {
					if(tCreditCard.getDeactivatedOn()==null) creditCards.add(tCreditCard);
				}
				// Store to request attributes, return success
				request.setAttribute("business", account);
				request.setAttribute("creditCards", creditCards);
				request.setAttribute("lastPaymentCreditCard", creditCard.getProviderUniqueId());
				request.setAttribute("errorReason", authorizationResult.getErrorCode().toString());
				return mapping.findForward("error");
			}
			case SUCCESS :
				// Check approval result
				switch(authorizationResult.getApprovalResult()) {
					case HOLD :
					{
						aoTransaction.held(Integer.parseInt(transaction.getPersistenceUniqueId()));
						request.setAttribute("business", account);
						request.setAttribute("creditCard", creditCard);
						request.setAttribute("transaction", transaction);
						request.setAttribute("aoTransaction", aoTransaction);
						request.setAttribute("reviewReason", authorizationResult.getReviewReason().toString());
						return mapping.findForward("hold");
					}
					case DECLINED :
					{
						// Update transaction as declined
						aoTransaction.declined(Integer.parseInt(transaction.getPersistenceUniqueId()));
						// Get the list of active credit cards stored for this business
						List<CreditCard> allCreditCards = account.getCreditCards();
						List<CreditCard> creditCards = new ArrayList<>(allCreditCards.size());
						for(CreditCard tCreditCard : allCreditCards) {
							if(tCreditCard.getDeactivatedOn()==null) creditCards.add(tCreditCard);
						}
						// Store to request attributes, return success
						request.setAttribute("business", account);
						request.setAttribute("creditCards", creditCards);
						request.setAttribute("lastPaymentCreditCard", creditCard.getProviderUniqueId());
						request.setAttribute("declineReason", authorizationResult.getDeclineReason().toString());
						return mapping.findForward("declined");
					}
					case APPROVED :
					{
						if(MakePaymentNewCardCompletedAction.DEBUG_AUTHORIZE_THEN_CAPTURE) {
							// Perform capture in second step
							CaptureResult captureResult = rootProcessor.capture(principal, transaction);
							switch(captureResult.getCommunicationResult()) {
								case LOCAL_ERROR :
								case IO_ERROR :
								case GATEWAY_ERROR :
								{
									// Update transaction as failed
									aoTransaction.declined(Integer.parseInt(transaction.getPersistenceUniqueId()));
									// Get the list of active credit cards stored for this business
									List<CreditCard> allCreditCards = account.getCreditCards();
									List<CreditCard> creditCards = new ArrayList<>(allCreditCards.size());
									for(CreditCard tCreditCard : allCreditCards) {
										if(tCreditCard.getDeactivatedOn()==null) creditCards.add(tCreditCard);
									}
									// Store to request attributes, return success
									request.setAttribute("business", account);
									request.setAttribute("creditCards", creditCards);
									request.setAttribute("lastPaymentCreditCard", creditCard.getProviderUniqueId());
									request.setAttribute("errorReason", authorizationResult.getErrorCode().toString());
									return mapping.findForward("error");
								}
								case SUCCESS :
								{
									// Continue with processing of SUCCESS below, same as used for direct sale(...)
									break;
								}
								default:
									throw new RuntimeException("Unexpected value for capture communication result: "+captureResult.getCommunicationResult());
							}
						}
						// Update transaction as successful
						aoTransaction.approved(Integer.parseInt(transaction.getPersistenceUniqueId()));
						request.setAttribute("business", account);
						request.setAttribute("creditCard", creditCard);
						request.setAttribute("transaction", transaction);
						request.setAttribute("aoTransaction", aoTransaction);
						return mapping.findForward("success");
					}
					default:
						throw new RuntimeException("Unexpected value for authorization approval result: "+authorizationResult.getApprovalResult());
				}
			default:
				throw new RuntimeException("Unexpected value for authorization communication result: "+authorizationResult.getCommunicationResult());
		}
	}
}
