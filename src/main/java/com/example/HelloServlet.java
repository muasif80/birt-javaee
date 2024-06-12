package com.example;

import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.exception.BirtException;

import com.example.birt.engine.dto.OutputType;
import com.example.birt.engine.service.BirtReportService;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        response.getWriter().println("<h1>Good Bye!</h1>");
		System.out.println("Hello I am here");
        
        ServletContext sc = getServletContext();
        BirtReportService birtReportService;
		try {
			birtReportService = new BirtReportService(sc);
			birtReportService.generateMasterDetailSingleReportPDF("master_child_report_on_single_page", OutputType.PDF, response, request);
		} catch (BirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}