package org.rboug.application.elibrary.view.shopping;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jasperreports.engine.*;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;


@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)

@Named
@RequestScoped
public class ShowPdfBean {
    private String PATH_OF_REPORTS_JASPER = "C:\\Users\\Rbougrin\\JaspersoftWorkspace\\MyReports";
    public String viewReportPDF() throws SQLException, JRException, IOException, ClassNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/elibrary";

        // - Connexion à la base
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","postgres");
        //props.setProperty("ssl","true");
        Connection conn = DriverManager.getConnection(url, props);

        File file = new File(PATH_OF_REPORTS_JASPER);
        HashMap hm = new HashMap();
        hm.put("INVOICE_ID",(long)751);
        hm.put("subReport", PATH_OF_REPORTS_JASPER+"\\Blank_A4_3" + ".jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                new FileInputStream(new File(file, "Blank_A4_2" + ".jasper")),
                hm, conn);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context
                .getExternalContext().getResponse();
        /***********************************************************************
         * Pour afficher une boîte de dialogue pour enregistrer le fichier sous
         * le nom rapport.pdf
         **********************************************************************/
        response.addHeader("Content-disposition",
                "attachment;filename=rapport.pdf");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.setContentType("application/pdf");
        context.responseComplete();
        return null;
    }
}
