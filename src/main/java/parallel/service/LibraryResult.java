package parallel.service;

import structuredData.Libraries;

public record LibraryResult(String llmName, Libraries response) {
    @Override
    public String toString() {
        return "--- " +
               llmName +
               " ---\n" +
               response +
               "\n\n";
    }
}
