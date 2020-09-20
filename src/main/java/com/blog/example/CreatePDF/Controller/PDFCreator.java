package com.blog.example.CreatePDF.Controller;

import com.blog.example.CreatePDF.Service.PdfService;
import com.blog.example.CreatePDF.Utils.ZipUtil;
import com.blog.example.CreatePDF.dto.UserInfo;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.List;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/testapp")
@Controller
public class PDFCreator {

    private static final String UTF_8 = "UTF-8";
    private final SpringTemplateEngine templateEngine;
    private final PdfService pdfService;
    private final ZipUtil zipUtil;

    /**
     * http://localhost:8080/testapp/file/my_pledge.pdf
     *
     * @param fileName
     * @param response
     * @throws IOException
     * @throws DocumentException
     */
    @GetMapping("/file/{fileName:.+}")
    public void downloadPDFResource(@PathVariable("fileName") String fileName, HttpServletResponse response)
            throws IOException, DocumentException {
        // a web service or whatever.
        UserInfo data = pdfService.getUserInfo(); // get data from database
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
        renderer.getFontResolver().addFont("templates/font/NanumGothic.ttf", IDENTITY_H, EMBEDDED);
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

    /**
     * http://localhost:8080/testapp/files/my_pledge.zip
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/files/{fileName:.+}")
    public void downloadZip(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        List<UserInfo> userList = pdfService.getUserInfoList(); // get data from database
        zipUtil.downloadZipFile(fileName, userList, response);
    }


}
