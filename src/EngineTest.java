import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void encrypt() {
    }

    @org.junit.jupiter.api.Test
    void decrypt() throws Exception {
        Engine e = new Engine();
        String s = Engine.encrypt("Hallo", "123");
        String d = Engine.decrypt(s, "123");
        assertEquals("Hallo", d);
    }
    @org.junit.jupiter.api.Test
    void decrypt2() throws Exception {
        Engine e = new Engine();
        String s = Engine.encrypt("facebook", "123");
        String d = Engine.decrypt(s, "123");
        assertEquals("facebook", d);
    }
    void decrypt3() throws Exception {
        Engine e = new Engine();
        String d = Engine.decrypt("dcKJSchtlsM46OhLHWbi9w==", "123");
        System.out.println(d);
        System.out.println("Hallo");
        assertEquals("facebook", d);
    }
}