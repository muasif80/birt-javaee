package com.example.birt.engine.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;


import com.example.birt.engine.dto.OutputType;
import com.example.birt.engine.dto.Report;
import com.example.birt.engine.dto.Report.Parameter;


import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;


public class BirtReportService {
	
	public static Log log = LogFactory.getLog(BirtReportService.class);
    
	
	String reportsPath = "";
	String imagesPath = "";
	
    private HTMLServerImageHandler htmlImageHandler = new HTMLServerImageHandler();


    private IReportEngine birtEngine;
    private String imageFolder;

    private Map<String, IReportRunnable> reports = new HashMap<>();

    
    public BirtReportService(ServletContext sc) throws BirtException, MalformedURLException {
    	
//    	private String reportsPath = "C:/Users/asif/Desktop/00_trainings/code/spring-boot-mvc-birt";
//      private String imagesPath  = reportsPath + "/images";
        
       
    	 reportsPath = sc.getResource("/reports").getPath();
    	 log.debug("reportsPath " + reportsPath);
    	 System.out.println("reportsPath " + reportsPath);
    	 
    	 imagesPath = sc.getResource("/reports/images").getPath();
    	 log.debug("imagesPath " + imagesPath);
    	 System.out.println("imagesPath " + imagesPath);
    	 
        EngineConfig config = new EngineConfig();
        config.getAppContext().put("servlet", sc);
        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        birtEngine = factory.createReportEngine(config);
        imageFolder = System.getProperty("user.dir") + File.separatorChar + reportsPath + imagesPath;
        loadReports();
    }

    /**
     * Load report files to memory
     *
     */
    public void loadReports() throws EngineException {
        File folder = new File(reportsPath);
        for (String file : Objects.requireNonNull(folder.list())) {
        	System.out.println("Adding report " + file);
            if (!file.endsWith(".rptdesign")) {
                continue;
            }

            reports.put(file.replace(".rptdesign", ""),
              birtEngine.openReportDesign(folder.getAbsolutePath() + File.separator + file));

        }
        
    }

    public List<Report> getReports() {
        List<Report> response = new ArrayList<>();
        for (Map.Entry<String, IReportRunnable> entry : reports.entrySet()) {
            IReportRunnable report = reports.get(entry.getKey());
            IGetParameterDefinitionTask task = birtEngine.createGetParameterDefinitionTask(report);
            Report reportItem = new Report(report.getDesignHandle().getProperty("title").toString(), entry.getKey());
            for (Object h : task.getParameterDefns(false)) {
                IParameterDefn def = (IParameterDefn) h;
                List<Parameter> parameters = reportItem.getParameters();
                if(parameters != null) {
                	reportItem.getParameters()
                		.add(new Report.Parameter(def.getPromptText(), def.getName(), getParameterType(def)));
                }
            }
            response.add(reportItem);
        }
        return response;
    }

    private Report.ParameterType getParameterType(IParameterDefn param) {
        if (IParameterDefn.TYPE_INTEGER == param.getDataType()) {
            return Report.ParameterType.INT;
        }
        return Report.ParameterType.STRING;
    }

    public void generateMainReport(String reportName, OutputType output, HttpServletResponse response, HttpServletRequest request) {
        switch (output) {
        case HTML:
            generateHTMLReport(reports.get(reportName), response, request);
            break;
        case PDF:
            generatePDFReport(reports.get(reportName), response, request);
            break;
        default:
            throw new IllegalArgumentException("Output type not recognized:" + output);
        }
    }
    
    public void generateMasterDetailSingleReportPDF(String reportName, OutputType output, HttpServletResponse response, HttpServletRequest request) throws EngineException {
    	File folder = new File(reportsPath);
    	System.out.println("folder " + folder);
    	IReportRunnable report = birtEngine.openReportDesign(folder.getAbsolutePath() + File.separator + "Cust_Orders_Payments.rptdesign");
    	generatePDFReport(report, response, request);
    }

    /**
     * Generate a report as HTML
     */
    @SuppressWarnings("unchecked")
    private void generateHTMLReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        response.setContentType(birtEngine.getMIMEType("html"));
        IRenderOption options = new RenderOption();
        HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
        htmlOptions.setOutputFormat("html");
        htmlOptions.setBaseImageURL("/" + reportsPath + imagesPath);
        htmlOptions.setImageDirectory(imageFolder);
        htmlOptions.setImageHandler(htmlImageHandler);
        runAndRenderTask.setRenderOption(htmlOptions);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
        
//        For orders_by_customer report - Set parameter cn
        Map<String, Object> params = new HashMap<>();
        params.put("cn", 112);
        runAndRenderTask.setParameterValues(params);

        try {
            htmlOptions.setOutputStream(response.getOutputStream());
            runAndRenderTask.run();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            runAndRenderTask.close();
        }
    }

    /**
     * Generate a report as PDF
     */
    @SuppressWarnings("unchecked")
    private void generatePDFReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
    	System.out.println("report " + report);
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        response.setContentType(birtEngine.getMIMEType("pdf"));
        IRenderOption options = new RenderOption();
        PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
        pdfRenderOption.setOutputFormat("pdf");
        runAndRenderTask.setRenderOption(pdfRenderOption);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);
        
//      For orders_by_customer report - Set parameter cn
      Map<String, Object> params = new HashMap<>();
      params.put("cn", 112);
      runAndRenderTask.setParameterValues(params);

        try {
            pdfRenderOption.setOutputStream(response.getOutputStream());
            runAndRenderTask.run();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            runAndRenderTask.close();
        }
    }
    
    

    public void destroy() {
        birtEngine.destroy();
        Platform.shutdown();
    }
}
