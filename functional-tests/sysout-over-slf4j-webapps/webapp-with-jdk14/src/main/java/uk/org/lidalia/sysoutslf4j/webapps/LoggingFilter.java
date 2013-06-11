package uk.org.lidalia.sysoutslf4j.webapps;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.org.lidalia.lang.Exceptions.throwUnchecked;

public class LoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to do
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable t) {
            LOG.error("Uncaught exception handling request " + requestLine((HttpServletRequest) servletRequest), t);
            throwUnchecked(t);
        }
    }

    private String requestLine(HttpServletRequest httpRequest) {
        final StringBuffer requestURL = httpRequest.getRequestURL();
        final String queryString = httpRequest.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            requestURL.append('?').append(queryString);
        }
        return httpRequest.getMethod() + " " + requestURL;
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
