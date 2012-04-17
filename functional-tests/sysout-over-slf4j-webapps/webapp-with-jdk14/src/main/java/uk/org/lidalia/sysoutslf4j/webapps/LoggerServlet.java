package uk.org.lidalia.sysoutslf4j.webapps;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Optional.fromNullable;

public class LoggerServlet extends HttpServlet {

    private static final long serialVersionUID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loggerName = fromNullable(request.getParameter("logger")).or(Logger.ROOT_LOGGER_NAME);
        boolean useNative = Boolean.valueOf(request.getParameter("native"));
        String message = request.getParameter("message");
        if (useNative) {
            java.util.logging.Logger.getLogger(loggerName).info(message);
        } else {
            LoggerFactory.getLogger(loggerName).info(message);
        }
    }
}
