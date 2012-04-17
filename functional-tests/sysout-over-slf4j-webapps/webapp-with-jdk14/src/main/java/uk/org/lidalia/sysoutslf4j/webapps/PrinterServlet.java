package uk.org.lidalia.sysoutslf4j.webapps;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import static com.google.common.base.Optional.fromNullable;

public class PrinterServlet extends HttpServlet {

    private static final long serialVersionUID = 1;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = fromNullable(request.getParameter("output")).or("System.out");
        SystemOutput output = SystemOutput.findByName(name);
        output.get().println(request.getParameter("message"));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
