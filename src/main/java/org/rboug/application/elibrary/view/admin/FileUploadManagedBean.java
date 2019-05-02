package org.rboug.application.elibrary.view.admin;

import org.primefaces.model.UploadedFile;

import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Named;
import javax.faces.annotation.FacesConfig;
import java.io.Serializable;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;


@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@SessionScoped
public class FileUploadManagedBean implements Serializable {
    UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String dummyAction(){
        System.out.println("Uploaded File Name Is :: "+file.getFileName()+" :: Uploaded File Size :: "+file.getSize());
        return "";
    }
}
