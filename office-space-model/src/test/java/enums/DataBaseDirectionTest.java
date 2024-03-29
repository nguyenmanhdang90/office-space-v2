package enums;

import com.ourfancyteamname.officespace.enums.DataBaseDirection;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

public class DataBaseDirectionTest {
  public static final String[] names = {"ASC", "DESC"};

  @Test
  public void name() {
    Stream.of(DataBaseDirection.values())
        .map(Enum::name)
        .forEach(s -> Assert.assertTrue(ArrayUtils.contains(names, s)));
  }
}
