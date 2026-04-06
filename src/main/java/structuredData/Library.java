package structuredData;

public record Library(String name, String provider, String url, String language, String version) {
    @Override
    public String toString() {
        return name + " | " + provider +
               ", url='" + url + '\'' +
               ", language='" + language + '\'' +
               ", version='" + version + '\'' +
               '}';
    }
}
