package guru.qa.niffler.config;

public enum LocalConfig implements Config{
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @Override
    public String spendUrl() {
        return "http://127.0.0.1:8093/";
    }
}
