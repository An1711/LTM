package config;

import javax.servlet.ServletContext;

public class AppConfig {

    public static String getServerHost(ServletContext ctx) {
        return ctx.getInitParameter("SERVER_HOST");
    }

    public static int getServerPort(ServletContext ctx) {
        return Integer.parseInt(ctx.getInitParameter("SERVER_PORT"));
    }

    public static String getWorkerHost(ServletContext ctx) {
        return ctx.getInitParameter("WORKER_HOST");
    }

    public static int getWorkerPort(ServletContext ctx) {
        return Integer.parseInt(ctx.getInitParameter("WORKER_PORT"));
    }
}
