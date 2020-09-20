package com.blog.example.CreatePDF.Utils;

import com.blog.example.CreatePDF.dto.UserInfo;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;

@Slf4j
@RequiredArgsConstructor
@Component
public class ZipUtil {

    private final SpringTemplateEngine templateEngine;
    private static final String PDF_EXT = "pdf";
    private static final String UTF_8 = "UTF-8";

    /**
     * download script zip file
     *
     * @param zipName
     * @param userList
     * @param response
     */
    public void downloadZipFile(String zipName, List<UserInfo> userList, HttpServletResponse response) {
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());) {
            response.setHeader("Content-Disposition", "attachment; filename=" + zipName + ";");
            response.setHeader("Content-Transfer-Encoding", "binary");
            zos.setLevel(8); // 압축 레벨 - 최대 압축률은 9, 디폴트 8

            for (UserInfo data : userList) {
                Context context = new Context();
                context.setVariable("data", data);

                String renderedHtmlContent = templateEngine.process("/pdf/template", context);
                String xHtml = convertToXhtml(renderedHtmlContent);

                ByteArrayOutputStream bos = generatePdfDocumentBaos(xHtml);
                byte[] pdfReport = bos.toByteArray();
                this.packFilesToZip(pdfReport, data.getLastname(), zos);
            }

        } catch (IOException | DocumentException e) {
            // throw new FileException(ExceptionCode.E00040007, e);
            e.printStackTrace();
        }
    }


    /**
     * packing scripts into zip
     *
     * @param pdfBytes
     * @param filename
     * @param zos
     */
    public void packFilesToZip(byte[] pdfBytes, String filename, ZipOutputStream zos) {
        int bufferSize = 1024 * 2;

        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(pdfBytes))) {
            ZipEntry zentry = new ZipEntry(filename + "." + PDF_EXT);
            zos.putNextEntry(zentry);

            byte[] buffer = new byte[bufferSize];
            int cnt = 0;
            while ((cnt = bis.read(buffer, 0, bufferSize)) != -1) {
                zos.write(buffer, 0, cnt);
            }
            zos.closeEntry();
        } catch (Exception e) {
            // throw new FileException(ExceptionCode.E00040004, e);
            e.printStackTrace();
        }

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

}
