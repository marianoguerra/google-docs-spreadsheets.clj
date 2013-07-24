(ns marianoguerra.google-docs-spreadsheets-cli
  (:use marianoguerra.google-docs-spreadsheets
        [clojure.tools.cli :only [cli]]
        [clojure.pprint :only [pprint]])
  (:gen-class))

(defn- parse-int [value]
  (Integer/parseInt value))

(defn- read-input [title]
  (print title)
  (flush)
  (read-line))

(defn- read-integer [title]
  (parse-int (read-input title)))

(defn- list-titles [things]
  (map-indexed
    (fn [i thing]
      [(str i ". ") (get-title thing)])
    things))

(defn- print-list [files]
  (doseq [[index title] (list-titles files)]
    (println index title)))

(defn parse-args [args]
  (cli args
       ["-s" "--sheet-id" "Spreadsheet id"] 
       ["-w" "--worksheet-id" "Worksheet id"] 
       ["-u" "--username" "Username to authenticate"]
       ["-p" "--password" "Password for authentication"]))

(defn main [& args]
  (let [[{:keys [sheet-id worksheet-id username password] :as opts} rest-args usage] (parse-args args)
        username (or username (read-input "Username: "))
        password (or password (read-input "Password: "))
        service (get-service username password "google-docs-spreadsheets")]

    (if (and sheet-id worksheet-id)
      (pprint (get-cells-from-ids service sheet-id worksheet-id 1 1 10 10))

      (let [files (list-files service)
            _ (print-list files)
            file-number (read-integer "Spreadsheet Number: ")
            file (.get files file-number)
            worksheets (get-worksheets file)
            _ (print-list worksheets)
            worksheet-number (read-integer "Worksheet Number: ")
            worksheet (.get worksheets worksheet-number)]
        (pprint (to-spreadsheet file))
        (pprint (to-worksheet worksheet))
        (pprint (get-cells service worksheet 1 1 10 10))))))

