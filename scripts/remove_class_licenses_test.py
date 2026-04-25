import importlib.util
import pathlib
import unittest


SCRIPT_PATH = pathlib.Path(__file__).with_name("remove_class_licenses.py")


def load_script():
    spec = importlib.util.spec_from_file_location("remove_class_licenses", SCRIPT_PATH)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class RemoveClassLicensesTest(unittest.TestCase):
    def test_removes_leading_mozilla_license_block(self):
        script = load_script()
        source = """/*
 * Mozilla Public License
 * Version 2.0
 */

package example;

public class Example {}
"""

        cleaned, changed = script.remove_leading_license(source)

        self.assertTrue(changed)
        self.assertEqual(
            """package example;

public class Example {}
""",
            cleaned,
        )

    def test_leaves_non_leading_license_text_unchanged(self):
        script = load_script()
        source = """package example;

public class Example {
    String text = "Mozilla Public License";
}
"""

        cleaned, changed = script.remove_leading_license(source)

        self.assertFalse(changed)
        self.assertEqual(source, cleaned)


if __name__ == "__main__":
    unittest.main()
