package org.rboug.application.elibrary.view.shopping;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

    private Long invoice_id;

    public Long getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(Long invoice_id) {
        this.invoice_id = invoice_id;
    }

    String PATH_OF_REPORT_JASPER;

    public String getPathFromWebContentFolder(String filePath) {
        try {
            return FacesContext.getCurrentInstance().getExternalContext().getResource(filePath).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String viewReportPDF() throws SQLException, JRException, IOException, ClassNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/elibrary";
        //Long id = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"));
        // - Connexion à la base
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "admin");
        //props.setProperty("ssl","true");
        Connection conn = DriverManager.getConnection(url, props);

        String myFileName1 = getPathFromWebContentFolder("./resources/jasperReports/SubReport_Invoice.jasper");
        String myFileName2 = getPathFromWebContentFolder("./resources/jasperReports/Invoice.jasper");


        File file = new File(myFileName2);
        HashMap hm = new HashMap();
        Long id = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("id");
        hm.put("INVOICE_ID", id);
        hm.put("subReport", myFileName1);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                new FileInputStream(new File(file, "Invoice" + ".jasper")),
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
