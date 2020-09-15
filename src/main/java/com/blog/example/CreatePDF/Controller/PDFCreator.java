package com.blog.example.CreatePDF.Controller;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;


@RequiredArgsConstructor
@RequestMapping("/testapp")
@Controller
public class PDFCreator {

    private static final String UTF_8 = "UTF-8";
    private final SpringTemplateEngine templateEngine;

    /**
     * http://localhost:8080/testapp/file/my_pledge.pdf
     *
     * @param fileName
     * @param response
     * @throws IOException
     * @throws DocumentException
     */
    @RequestMapping("/file/{fileName:.+}")
    public void downloadPDFResource(@PathVariable("fileName") String fileName, HttpServletResponse response)
            throws IOException, DocumentException {
        // a web service or whatever.
        Data data = exampleDataForJohnDoe();
        Context context = new Context();
        context.setVariable("data", data);

        String renderedHtmlContent = templateEngine.process("/pdf/template", context);
        String xHtml = convertToXhtml(renderedHtmlContent);

        ByteArrayOutputStream bos = generatePdfDocumentBaos(xHtml);
        byte[] pdfReport = bos.toByteArray();

        String mimeType = "application/pdf";
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        response.setContentLength(pdfReport.length);

        ByteArrayInputStream inStream = new ByteArrayInputStream(pdfReport);
        FileCopyUtils.copy(inStream, response.getOutputStream());
    }

    public ByteArrayOutputStream generatePdfDocumentBaos(String html) throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("templates/fonts/Code39.ttf", IDENTITY_H, EMBEDDED);
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(baos);
        baos.close();
        return baos;
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

    private Data exampleDataForJohnDoe() {
        Data data = new Data();
        data.setFirstname("Lee");
        data.setLastname("Yu Pyeong");
        data.setStreet("Example Street 1001");
        data.setZipCode("12345");
        data.setCity("Annoy City");
        return data;
    }

    static class Data {
        private String firstname;
        private String lastname;
        private String street;
        private String zipCode;
        private String city;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }


}
