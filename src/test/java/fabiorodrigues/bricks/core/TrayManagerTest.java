package fabiorodrigues.bricks.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fabiorodrigues.bricks.components.NotificationType;
import org.junit.jupiter.api.Test;

class TrayManagerTest {

    @Test
    void deveIdentificarMacOsPeloNomeDoSistema() {
        assertTrue(TrayManager.isMacOsName("Mac OS X"));
        assertTrue(TrayManager.isMacOsName("macOS"));
        assertFalse(TrayManager.isMacOsName("Windows 11"));
        assertFalse(TrayManager.isMacOsName("Linux"));
        assertFalse(TrayManager.isMacOsName(null));
    }

    @Test
    void deveEscaparStringParaAppleScript() {
        assertEquals("\"Mensagem\"", TrayManager.toAppleScriptString("Mensagem"));
        assertEquals("\"\"", TrayManager.toAppleScriptString(null));
        assertEquals("\"Ele disse \\\"ola\\\"\"", TrayManager.toAppleScriptString("Ele disse \"ola\""));
        assertEquals("\"C:\\\\temp\\\\ficheiro.txt\"",
            TrayManager.toAppleScriptString("C:\\temp\\ficheiro.txt"));
    }

    @Test
    void deveMapearTipoParaSubtituloMacOs() {
        assertEquals("Sucesso", TrayManager.toMacSubtitle(NotificationType.SUCCESS));
        assertEquals("Informacao", TrayManager.toMacSubtitle(NotificationType.INFO));
        assertEquals("Aviso", TrayManager.toMacSubtitle(NotificationType.WARNING));
        assertEquals("Erro", TrayManager.toMacSubtitle(NotificationType.ERROR));
    }
}
