/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import static com.aoindustries.website.ApplicationResources.accessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * During the processing of the skin, page attributes are built and stored here, one instance per request.
 *
 * @author  AO Industries, Inc.
 */
public class PageAttributes {

	/**
	 * The possible values for layout.
	 */
	public static final String
		LAYOUT_NORMAL = "normal",
		LAYOUT_MINIMAL = "minimal"
	;

	/**
	 * The following key is used to store the objects in the page attributes.
	 */
	public static final String ATTRIBUTE_KEY = "pageAttributes";

	/**
	 * The following scope is used to store the objects in the page attributes.
	 */
	public static final int ATTRIBUTE_SCOPE = PageContext.REQUEST_SCOPE;

	public static class Link {

		/**
		 * This matches the list documented in aoweb-struts-skin.tld
		 */
		public static boolean isValidConditionalCommentExpression(String conditionalCommentExpression) {
			return
				"IE 6".equals(conditionalCommentExpression)
				|| "IE 7".equals(conditionalCommentExpression)
				|| "IE 8".equals(conditionalCommentExpression)
			;
		}

		private final String rel;
		private final String href;
		private final String type;
		private final String conditionalCommentExpression;

		Link(String rel, String href, String type, String conditionalCommentExpression) {
			this.rel = rel;
			this.href = href;
			this.type = type;
			if(conditionalCommentExpression==null || isValidConditionalCommentExpression(conditionalCommentExpression)) {
				this.conditionalCommentExpression = conditionalCommentExpression;
			} else {
				throw new IllegalArgumentException(
					accessor.getMessage(
						"Invalid value for conditional comment expression.  Please refer to aoweb-struts-skin.tld for the valid values.",
						Locale.getDefault(),
						"skintags.PageAttributes.Link.conditionalCommentExpression.invalid"
					)
				);
			}
		}

		public String getRel() {
			return rel;
		}

		/**
		 * Gets the already URL-encoded href.
		 */
		public String getHref() {
			return href;
		}

		public String getType() {
			return type;
		}

		public String getConditionalCommentExpression() {
			return conditionalCommentExpression;
		}
	}

	private final HttpServletRequest request;
	private String path;
	private String keywords;
	private String description;
	private String author;
	private String copyright;
	private List<Meta> metas;
	private List<Meta> unmodifiableMetas;
	private List<Link> links;
	private List<Link> unmodifiableLinks;
	private String title;
	private String navImageAlt;
	private List<Parent> parents;
	private List<Child> children;
	private String layout;
	private String onload;

	public PageAttributes(HttpServletRequest request) {
		this.request = request;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public List<Meta> getMetas() {
		if(metas==null) return Collections.emptyList();
		if(unmodifiableMetas==null) unmodifiableMetas = Collections.unmodifiableList(metas);
		return unmodifiableMetas;
	}

	public void addMeta(Meta meta) {
		if(metas==null) metas = new ArrayList<Meta>();
		metas.add(meta);
	}

	public List<Link> getLinks() {
		if(links==null) return Collections.emptyList();
		if(unmodifiableLinks==null) unmodifiableLinks = Collections.unmodifiableList(links);
		return unmodifiableLinks;
	}

	public void addLink(String rel, String href, String type, String conditionalCommentExpression) {
		if(links==null) links = new ArrayList<Link>();
		links.add(new Link(rel, href, type, conditionalCommentExpression));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNavImageAlt() {
		String myNavImageAlt = this.navImageAlt;
		if(myNavImageAlt==null || myNavImageAlt.length()==0) myNavImageAlt = this.title;
		return myNavImageAlt;
	}

	public void setNavImageAlt(String navImageAlt) {
		this.navImageAlt = navImageAlt;
	}

	public List<Parent> getParents() {
		if(parents==null) {
			List<Parent> emptyList = Collections.emptyList();
			return emptyList;
		}
		return parents;
	}

	public void addParent(Parent parent) {
		if(parents==null) parents = new ArrayList<Parent>();
		parents.add(parent);
	}

	/**
	 * Gets the direct children of this page.
	 */
	public List<Child> getChildren() {
		if(children==null) {
			List<Child> emptyList = Collections.emptyList();
			return emptyList;
		}
		return children;
	}

	public void addChild(Child child) {
		if(children==null) children = new ArrayList<Child>();
		children.add(child);
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		if(layout.equals(LAYOUT_NORMAL) || layout.equals(LAYOUT_MINIMAL)) this.layout = layout;
		else throw new IllegalArgumentException(accessor.getMessage("skintags.PageAttributes.setLayout.invalid"));
	}

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}
}
