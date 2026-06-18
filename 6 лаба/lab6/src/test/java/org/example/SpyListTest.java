package org.example;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
class SpyListTest { @Test void shouldVerifyAddCall(){ ArrayList<String> list = spy(new ArrayList<>()); list.add("one"); verify(list, times(1)).add("one"); } }