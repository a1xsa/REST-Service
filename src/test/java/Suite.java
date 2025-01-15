import org.junit.platform.suite.api.SelectPackages;

@org.junit.platform.suite.api.Suite
@SelectPackages({"dao", "service", "servlet"})
public class Suite {
}
