import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@org.junit.platform.suite.api.Suite
@SelectPackages({"DAOtest","ServiceTest","ServletTest"})
public class Suite {
}
