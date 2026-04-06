package structuredData;

import java.util.List;
import java.util.stream.Collectors;

public record Libraries(List<Library> libraries) {
    @Override
    public String toString() {
        return libraries.stream()
                .map(lib -> lib.toString())
                .collect(Collectors.joining("\n"));
    }
}
