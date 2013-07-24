(ns marianoguerra.google-docs-spreadsheets
  (:require [clojure.string :as string])
  (:import
    (java.net URL URI)
    (com.google.gdata.data.spreadsheet
      SpreadsheetFeed CellFeed)
    (com.google.gdata.client.spreadsheet
      SpreadsheetService)))

(def SPREADSHEET_FEED_URL
  (URL. "https://spreadsheets.google.com/feeds/spreadsheets/private/full"))

(defn get-workbook-cells-url [sheet-id book-id]
  (str "http://spreadsheets.google.com/feeds/cells/"
       sheet-id "/" book-id "/private/values"))

(defn get-service [username password name]
  "return Spreadsheet service instance already authenticated, may throw
  an exception if credentials are invalid"
  (let [service (SpreadsheetService. name)]
    (.setUserCredentials service username password)
    service))

(defn list-files [service]
  "get a list of all spreadsheet documents"
  (let [feed (.getFeed service SPREADSHEET_FEED_URL SpreadsheetFeed)
        entries (.getEntries feed)]
    entries))

(defn get-worksheets [file]
  "get a list of all worksheets from a document"
  (.getWorksheets file))

(defn get-title [thing]
  "return the title of the thing as a string"
  (-> thing (.getTitle) (.getPlainText)))

(defn- parse-int [value]
  (Integer/parseInt value))

(defn- get-last-id-part [id]
  (.substring id (+ (.lastIndexOf id "/") 1)))

(defn get-id [thing]
  "return the id of the thing as a string"
  (get-last-id-part (.getId thing)))

(defn to-cell [cell]
  "return a clojure friendly cell representation from the original object"
  (let [id (get-id cell)
        coords (map parse-int
                    (clojure.string/split (subs id 1) #"C"))]
        {:id id
         :updated (.getUpdated cell)
         :coords coords
         :row (first coords)
         :col (second coords)
         :address (get-title cell)
         :input-value (-> cell (.getCell) (.getInputValue))
         :value (-> cell (.getCell) (.getValue))}))

(defn to-worksheet [worksheet]
  "return a clojure friendly worksheet representation from the original object"
  {:title (get-title worksheet)
   :updated (.getUpdated worksheet)
   :rows (.getRowCount worksheet)
   :cols (.getColCount worksheet)
   :id (get-id worksheet)})

(defn to-spreadsheet [spreadsheet]
  "return a clojure friendly spreadsheet representation from the original object"
  {:title (get-title spreadsheet)
   :updated (.getUpdated spreadsheet)
   :worksheets (map to-worksheet (get-worksheets spreadsheet))
   :id (get-id spreadsheet)})

(defn get-cells-from-feed-url [service feed-url-str min-row min-col max-row
                               max-col]
  "return the cells from a worksheet using a feed url"
  (let [cell-feed-url-str (str feed-url-str
                               "?min-row=" min-row
                               "&min-col=" min-col
                               "&max-row=" max-row
                               "&max-col=" max-col)
        cell-feed-url (.toURL (URI. cell-feed-url-str))
        cell-feed (.getFeed service cell-feed-url CellFeed)
        entries (.getEntries cell-feed)]
    (map to-cell entries)))

(defn get-cells [service worksheet min-row min-col max-row max-col]
  "get cells from a worksheet using a worksheet object"
  (let [feed-url-str (-> worksheet (.getCellFeedUrl) (.toString))
        entries (get-cells-from-feed-url service feed-url-str min-row min-col
                                         max-row max-col)]
    entries))

(defn get-cells-from-ids [service sheet-id book-id min-row min-col max-row
                          max-col]
  "get cells from a worksheet using a worksheet id"
  (let [feed-url-str (get-workbook-cells-url sheet-id book-id)
        entries (get-cells-from-feed-url service feed-url-str min-row min-col
                                         max-row max-col)]
    entries))
