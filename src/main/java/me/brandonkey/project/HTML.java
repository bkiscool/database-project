package me.brandonkey.project;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Create html by using elements
 */
public enum HTML {
    LINE_BREAK("br"),
    PARAGRAPH("p"),
    PRE("pre"),
    BUTTON("button");

    public String open;
    public String close;
    
    private String element;
    private HashMap<String, String> attributes = new HashMap<>();

    HTML(String element)
    {
        this.element = element;
        this.open = "<" + element + ">";
        this.close = "</" + element + ">";
    }

    /**
     * Wrap a piece of text in an html element
     * 
     * @param text The text to wrap in the html element
     * @return html
     */
    String apply(String text)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<" + element);

        for (Entry<String, String> attribute : attributes.entrySet())
        {
            builder.append(String.format(" %s='%s'", attribute.getKey(), attribute.getValue()));
        }

        builder.append(">" + text + this.close);

        return builder.toString();
    }

    /**
     * Add attributes to an html element.
     * Apply the html element and attributes by using the apply method
     * 
     * @param attribute An attribute for an element
     * @param value The value for the attribute
     * @return This HTML enum with the attributes (use .apply() to apply)
     */
    HTML addAttribute(String attribute, String value)
    {
        this.attributes.put(attribute, value);
        return this;
    }
    
}
