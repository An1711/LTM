package controller;

public class RequestJob {
    public enum Status { PENDING, PROCESSING, DONE, FAILED }

    private final long id;
    private final int type;
    private final int userId;
    private final String originalFileName;
    private final String inputPath;
    private volatile Status status;
    private volatile String result;

    public RequestJob(long id, int type, int userId, String originalFileName, String inputPath) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.inputPath = inputPath;
        this.status = Status.PENDING;
    }

    public long getId() { return id; }
    public int getType() { return type; }
    public int getUserId() { return userId; }
    public String getOriginalFileName() { return originalFileName; }
    public String getInputPath() { return inputPath; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }
    public String getResult() { return result; }
    public void setResult(String r) { this.result = r; }
}
