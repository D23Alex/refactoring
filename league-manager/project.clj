(defproject league-manager "0.1.0-SNAPSHOT"
  :description "service used for managing a basketball league: teams/players/contracts/games"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/algo.generic "0.1.3"]
                 [ring/ring-jetty-adapter "1.12.2"]
                 [ring/ring-core "1.12.2"]
                 [compojure "1.7.1"]
                 [ring/ring-json "0.5.1"]
                 [clojure.java-time "1.4.3"]
                 [org.postgresql/postgresql "42.2.10"]
                 [com.github.seancorfield/next.jdbc "1.2.780"]
                 [com.github.seancorfield/honeysql "2.6.1243"]]
  :main ^:skip-aot league-manager.core
  :repl-options {:init-ns league-manager.core})
