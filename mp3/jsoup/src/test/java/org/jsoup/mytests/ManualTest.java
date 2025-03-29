package org.jsoup.mytests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class ManualTest {

    // Modified from the test testSetHtmlTitle() of src/test/java/org/jsoup/nodes/ElementTest.java
    private void testElementHtml(String html, String tagName, String expectedInnerHtml, boolean flag) {
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById("1");
        element.html("good");
        assertEquals("good", element.html());
        // If the element is not supposed to be the rawtext, then we need to check the innerHtml
        if (flag) {
            element.html("<i>bad</i>");
            assertEquals("&lt;i&gt;bad&lt;/i&gt;", element.html());
            Element head = doc.getElementById("2");
            head.html("<" + tagName + "><i>bad</i></" + tagName + ">");
            assertEquals(expectedInnerHtml, head.html());
        }
    }

    @Test public void myTest1() {
        testElementHtml("<html><head id=2><iframe id=1></iframe></head></html>", "iframe", "<iframe>&lt;i&gt;bad&lt;/i&gt;</iframe>", true);
        testElementHtml("<html><head id=2><noembed id=1></noembed></head></html>", "noembed", "<noembed>\n" + //
                        " &lt;i&gt;bad&lt;/i&gt;\n" + //
                        "</noembed>", true);
        testElementHtml("<html><head id=2><noframes id=1></noframes></head></html>", "noframes", "<noframes>\n" + //
                        " &lt;i&gt;bad&lt;/i&gt;\n" + //
                        "</noframes>", true);
        testElementHtml("<html><head id=2><style id=1></style></head></html>", "style", "<style><i>bad</i></style>", true);
        testElementHtml("<html><head id=2><xmp id=1></xmp></head></html>", "xmp", "<xmp>\n" + //
                        " &lt;i&gt;bad&lt;/i&gt;\n" + //
                        "</xmp>", true);
    }

    @Test public void myTest2() {
        testElementHtml("<html><head id=2><script id=1></script></head></html>", "script", "<script><i>bad</i></script>", true);
    }

    @Test public void myTest3() {
        testElementHtml("<html><head id=2><noscript id=1></noscript></head></html>", "noscript", "<i>bad</i>", false);
    }

    @Test public void myTest4() {
        testElementHtml("<html><head id=2><plaintext id=1></plaintext></head></html>", "plaintext", "<i>bad</i>", false);
    }
}