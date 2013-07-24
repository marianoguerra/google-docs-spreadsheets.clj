(defproject marianoguerra/google-docs-spreadsheets "0.1.0"
  :description "Library to interact with google spreadsheets"
  :url "http://github.com/marianoguerra/google-docs-spreadsheets"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main marianoguerra.google-docs-spreadsheets-cli/main
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.2"]
                 [gdata-core/gdata-core "1.0"]
                 [gdata-base/gdata-base "1.0"]
                 [gdata-client/gdata-client "1.0"]
                 [gdata-client-meta/gdata-client-meta "1.0"]
                 [google-oauth-client/google-oauth-client "1.15.0-rc"]
                 [google-http-client/google-http-client "1.15.0-rc"]

                 [gdata-docs/gdata-docs "3.0"]
                 [gdata-docs-meta/gdata-docs-meta "3.0"]
                 [gdata-spreadsheet/gdata-spreadsheet "3.0"]
                 [gdata-spreadsheet-meta/gdata-spreadsheet-meta "3.0"]
                 [guava/guava "11.0.2"]])

