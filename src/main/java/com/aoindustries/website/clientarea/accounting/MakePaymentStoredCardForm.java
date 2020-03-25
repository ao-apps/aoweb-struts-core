/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.exception.WrappedException;
import com.aoindustries.util.i18n.CurrencyUtil;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.util.i18n.ThreadLocale;
import com.aoindustries.website.SiteSettings;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Currency;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
public class MakePaymentStoredCardForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String account;
	private String currency;
	private String id;
	private String paymentAmount;

	public MakePaymentStoredCardForm() {
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setAccount("");
		setCurrency("");
		setId("");
		setPaymentAmount("");
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount.trim();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors == null) errors = new ActionErrors();
		try {
			Currency javaCurrency;
			if(GenericValidator.isBlankOrNull(currency)) {
				errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.currency.required"));
				javaCurrency = null;
			} else {
				ActionServlet myServlet = getServlet();
				if(myServlet != null) {
					AOServConnector rootConn = SiteSettings.getInstance(myServlet.getServletContext()).getRootAOServConnector();
					com.aoindustries.aoserv.client.billing.Currency aoservCurrency = rootConn.getBilling().getCurrency().get(currency);
					if(aoservCurrency != null) {
						javaCurrency = aoservCurrency.getCurrency();
					} else {
						errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.currency.invalid"));
						javaCurrency = null;
					}
				} else {
					try {
						javaCurrency = Currency.getInstance(currency);
					} catch(IllegalArgumentException e) {
						errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.currency.invalid"));
						javaCurrency = null;
					}
				}
			}
			if(GenericValidator.isBlankOrNull(paymentAmount)) {
				errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.required"));
			} else {
				try {
					BigDecimal pa = Money.parseMoneyAmount(
						ThreadLocale.get(),
						javaCurrency == null ? null : CurrencyUtil.getSymbol(javaCurrency),
						this.paymentAmount
					);
					if(pa == null) {
						errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.invalid"));
					} else {
						this.paymentAmount = pa.toPlainString();
						if(pa.signum() <= 0) {
							errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.mustBeGeaterThanZero"));
						}
						if(javaCurrency != null) {
							// Verify scale against currency
							Money money = new Money(javaCurrency, pa);
							this.paymentAmount = money.getValue().toPlainString();
						}
					}
				} catch(NumberFormatException err) {
					errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.invalid"));
				}
			}
			return errors;
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}
}
