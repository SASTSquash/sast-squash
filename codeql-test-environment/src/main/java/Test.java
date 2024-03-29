import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

class Test extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            doSomeWork();
        } catch (NullPointerException ex) {
            // BAD: printing a stack trace back to the response
            ex.printStackTrace(response.getWriter());
            return;
        }

        try {
            doSomeWork();
        } catch (NullPointerException ex) {
            // BAD: printing a stack trace back to the response
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    printTrace(ex));
            return;
        }

        try {
            doSomeWork();
        } catch (NullPointerException ex) {
            // BAD: printing a stack trace back to the response
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    printTrace2(ex));
            return;
        }

        try {
            doSomeWork();
        } catch (Throwable ex) {
            // BAD: printing an exception message back to the response
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ex.getMessage());
        }
    }

    private void doSomeWork() {

    }

    private static String printTrace(Throwable ex) {
        StringWriter content = new StringWriter();
        ex.printStackTrace(new PrintWriter(content));
        return content.toString();
    }

    private static String printTrace2(Throwable ex) {
        StringWriter content = new StringWriter();
        PrintWriter pw = new PrintWriter(content);
        ex.printStackTrace(pw);
        return content.toString();
    }
}
