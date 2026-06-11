# LangChain4j Examples

A Java project demonstrating LangChain4j patterns for interacting with multiple LLM providers.

## Prerequisites

- Java 25
- Maven
- API keys set as environment variables:
  - `ANTHROPIC_API_KEY`
  - `OPENAI_API_KEY`
  - `GOOGLE_API_KEY`
  - Ollama running locally (for Ollama examples)

## Project Structure

```
src/main/java/
├── fundamentals/       # Basic chat requests (Anthropic, OpenAI, Google, Ollama)
├── prompt/             # Prompt templates and structured prompts
├── systemMessage/      # Four ways to set a system message
├── memory/             # Chat memory patterns (no memory, window memory, AI service)
├── streaming/          # Streaming responses
├── structuredData/     # Extracting structured data (JSON → Java objects)
├── tools/              # Tool/function calling (Claude, OpenAI)
├── aiService/          # High-level AiServices abstraction
├── parallel/           # Parallel LLM calls (CompletableFuture, virtual threads)
└── util/               # Shared helpers
```

## LLM Providers

| Provider  | Dependency                    | Model used               |
|-----------|-------------------------------|--------------------------|
| Anthropic | `langchain4j-anthropic`       | claude-sonnet-4-6        |
| OpenAI    | `langchain4j-open-ai`         | gpt-4o-mini              |
| Google    | `langchain4j-google-ai-gemini`| gemini-2.0-flash         |
| Ollama    | `langchain4j-ollama`          | llama3.2 (local)         |

## Running Examples

Each class has a `main()` instance method. Run a class directly from your IDE or via Maven:

```bash
mvn compile exec:java -Dexec.mainClass="fundamentals.RequestAnthropic"
```

### Key Examples

**Fundamentals** — simple queries, logging, system messages, response metadata:
`fundamentals/RequestAnthropic.java`

**Prompt templates** — `PromptTemplate`, `@StructuredPrompt`, variable substitution:
`prompt/PromptTemplateExamples.java`

**Memory** — stateless vs. windowed memory vs. AiService with memory:
`memory/WithChatMemory.java`, `memory/AiServiceWithMemory.java`

**Tools** — weather tool called by Claude or OpenAI in a multi-turn conversation:
`tools/WithToolsClaude.java`, `tools/WithToolsOpenAI.java`

**Structured data** — LLM returns a typed `Libraries` object via AiServices:
`structuredData/GetLibrariesFromAnthropic.java`

**Parallel chains** — fan-out to three LLMs, consolidate with Claude:
`parallel/ParallelChainsWithCompletableFuture.java` (CompletableFuture)
`parallel/ParallelChainsWithVirtualThreads.java` (virtual threads)

## Build

```bash
mvn clean package
```

Output is a shaded JAR in `target/`.
