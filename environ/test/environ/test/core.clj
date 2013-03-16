(ns environ.test.core
  (:use clojure.test
        environ.core))

(deftest test-env
  (testing "env variables"
    (is (= (:user env) (System/getenv "USER")))
    (is (= (:java-arch env) (System/getenv "JAVA_ARCH"))))
  (testing "system properties"
    (is (= (:user-name env) (System/getProperty "user.name")))
    (is (= (:user-country env) (System/getProperty "user.country"))))
  (testing "properties file"
    (spit ".properties" "some.prop=some_value")
    (use 'environ.core :reload)
    (is (= (:some-prop env) "some_value")))
  (testing "env file"
    (spit ".lein-env" (prn-str {:foo "bar"}))
    (use 'environ.core :reload)
    (is (= (:foo env) "bar")))
  (testing "env file with irregular keys"
    (spit ".lein-env" (prn-str {:foo.bar "baz"}))
    (use 'environ.core :reload)
    (is (= (:foo-bar env) "baz"))))
