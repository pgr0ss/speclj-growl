(ns speclj.report.growl
  (:require [speclj.results :refer [categorize]]
            [speclj.platform :refer [format-seconds]]
            [speclj.reporting :refer [tally-time]]
            [speclj.report.progress :refer [describe-counts-for]]
            [clojure.java.io :refer [input-stream resource as-url]]
            [gntp])
  (:import [speclj.reporting Reporter]))

(def ^:private resource-stream (comp input-stream resource))

(defn- settings [type]
  (case type
    :pass {:name "Success" :icon (resource-stream "pass.png")}
    :fail {:name "Failure" :icon (resource-stream "fail.png")}
    :error  {:name "Error" :icon (resource-stream "fail.png")}))

(def ^:private notifiers
  ((gntp/make-growler "Speclj")
    :pass (settings :pass)
    :fail (settings :fail)
    :error (settings :error)))

(defn growl
  "Trigger notification of the appropriate type"
  [result message]
  (when notifiers
    (let [params (settings result)]
      ((result notifiers)
         (params :name) :text message :icon (params :icon)))))

(defn growl-message [results]
  (let [result-map (categorize results)
        result     (if (= 0 (count (:fail result-map))) :pass :fail)
        seconds    (format-seconds (tally-time results))
        outcome    (describe-counts-for result-map)
        message    (format "%s\nTook %s seconds" outcome seconds)]
    (growl result message)))

(deftype GrowlReporter []
    Reporter
    (report-message [this message])
    (report-description [this description])
    (report-pass [this result])
    (report-pending [this result])
    (report-fail [this result])
    (report-runs [this results] (growl-message results))
    (report-error [this error]
      (let [exception (.-exception error)]
        (growl :error (format "%s: %s"
                              (.getSimpleName (class exception))
                              (.getMessage exception))))))

(defn new-growl-reporter []
    (GrowlReporter.))
