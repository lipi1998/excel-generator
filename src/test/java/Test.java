import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Description:
 * @Author: LiRuite
 * @Date: 2023/3/15 15:40
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Path a = Files.createTempDirectory("供应商");
        System.out.println("a = " + a.toAbsolutePath());
    }

}
