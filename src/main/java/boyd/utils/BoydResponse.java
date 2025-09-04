package boyd.utils;

public final class BoydResponse {
    private final String message;
    private final boolean exit;
    private final boolean error;

    private BoydResponse(String m, boolean ex, boolean er) {
        this.message = m; this.exit = ex; this.error = er;
    }
    public static BoydResponse ok(String m)    {
        return new BoydResponse(m, false, false);
    }
    public static BoydResponse error(String m) {
        return new BoydResponse(m, false, true);
    }
    public static BoydResponse exit(String m)  {
        return new BoydResponse(m, true,  false);
    }

    public String message() {
        return message;
    }
    public boolean isExit() {
        return exit;
    }
    public boolean isError(){
        return error;
    }
}
