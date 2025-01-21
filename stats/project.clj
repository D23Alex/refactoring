(defproject stats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/algo.generic "0.1.3"]
                 [ring/ring-jetty-adapter "1.12.2"]
                 [ring/ring-core "1.12.2"]
                 [compojure "1.7.1"]
                 [ring/ring-json "0.5.1"]
                 [com.novemberain/monger "3.6.0"]
                 [clj-http "3.13.0"]
                 [cheshire "5.13.0"]
                 [com.github.ben-manes.caffeine/caffeine "3.0.5"]]
  :main ^:skip-aot stats.core
  :repl-options {:init-ns stats.core})
