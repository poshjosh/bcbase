package com.bc.html;

public class HtmlPageGen extends HtmlGen {

	private String doctype = null;
	private String contenttype = null;

	public HtmlPageGen() {
            this(null, null);
	}

	private HtmlPageGen(String docType, String contentType) {
                if(docType == null) {
                    docType = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
                }
                HtmlPageGen.this.setDoctype(docType);
                if(contentType == null) {
                    contentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>";
                }
                HtmlPageGen.this.setContenttype(contentType);
                
	}
        
	public CharSequence getPage(String title, CharSequence bodyContents) {
		return getPage(title, null, bodyContents);
	}

	public CharSequence getPage(String title, String heading, CharSequence bodyContents) {
		return getPage(doctype, contenttype, title, heading, bodyContents);
	}

	public CharSequence getPage(String doctype, String contentType, 
                String title, String heading, CharSequence bodyContents) {

		StringBuilder titleAndStyle = new StringBuilder();
                
		if (contentType != null && contentType.length() > 0)
			titleAndStyle.append(contentType);
                
		enclosingTag("title", title, titleAndStyle);
                
		StringBuilder headAndBody = new StringBuilder();
                
		enclosingTag("head", titleAndStyle, headAndBody);
                
                if(heading != null) {
                        bodyContents = ((new StringBuilder(heading)).append("<br/>").append(bodyContents));                    
                }
		
                enclosingTag("body", bodyContents, headAndBody);
		
                StringBuilder page = new StringBuilder();
                
                if (doctype != null && doctype.length() > 0)
			page.append(doctype);
		
                return enclosingTag("html", headAndBody, page);
	}

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

	public String getDoctype() {
		return doctype;
	}

	public String getContentType() {
		return contenttype;
	}
}
