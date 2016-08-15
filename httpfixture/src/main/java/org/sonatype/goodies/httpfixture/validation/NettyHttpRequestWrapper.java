package org.sonatype.goodies.httpfixture.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import io.netty.handler.codec.http.HttpRequest;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps netty api.
 *
 * @since 2.2.0
 */
public class NettyHttpRequestWrapper
    implements HttpServletRequest
{

  private final HttpRequest httpRequest;

  public NettyHttpRequestWrapper(HttpRequest httpRequest) {
    checkNotNull(httpRequest);
    this.httpRequest = httpRequest;
  }

  @Override
  public String getHeader(String name) {
    return httpRequest.headers().get(name);
  }

  @Override
  public Enumeration<String> getHeaders(final String name) {
    return new Enumeration<String>()
    {

      private final Iterator<String> headers = httpRequest.headers().getAll(name).iterator();

      @Override
      public boolean hasMoreElements() {
        return headers.hasNext();
      }

      @Override
      public String nextElement() {
        return headers.next();
      }
    };
  }

  @Override
  public String getMethod() {
    return httpRequest.getMethod().name();
  }

  @Override
  public Object getAttribute(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getContentLength() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getContentLengthLong() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContentType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getParameter(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<String> getParameterNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String[] getParameterValues(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getProtocol() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getScheme() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getServerName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getServerPort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRemoteAddr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRemoteHost() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setAttribute(String name, Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeAttribute(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Locale getLocale() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<Locale> getLocales() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSecure() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRealPath(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRemotePort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalAddr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getLocalPort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletContext getServletContext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
      throws IllegalStateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncStarted() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncSupported() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DispatcherType getDispatcherType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAuthType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Cookie[] getCookies() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getDateHeader(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getIntHeader(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPathInfo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPathTranslated() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContextPath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getQueryString() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRemoteUser() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isUserInRole(String role) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Principal getUserPrincipal() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRequestedSessionId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRequestURI() {
    throw new UnsupportedOperationException();
  }

  @Override
  public StringBuffer getRequestURL() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getServletPath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpSession getSession(boolean create) {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpSession getSession() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String changeSessionId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void login(String username, String password) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void logout() throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

}
