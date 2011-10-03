(ns speclj.report.growl
  (:use
    [speclj.results :only (categorize)]
    [speclj.reporting :only (tally-time)]
    [speclj.report.progress :only (describe-counts-for)]
    [speclj.util :only (seconds-format)]
    [clj-growl.core :only (make-growler)])
  (:import
    [speclj.reporting Reporter]))

(def growl (make-growler "" "speclj" ["Message" true]))

(defn growl-message [results]
  (let [result-map (categorize results)
        tally (apply hash-map (interleave (keys result-map) (map count (vals result-map))))
        title (if (zero? (:fail tally)) "Success" "Failure")
        duration (.format seconds-format (tally-time results))
        counts (describe-counts-for result-map)]
    ["Message" title (format "Finished in %s seconds\n%s" duration counts)]))

(deftype GrowlReporter []
    Reporter
    (report-message [this message])
    (report-description [this description])
    (report-pass [this result])
    (report-pending [this result])
    (report-fail [this result])
    (report-runs [this results]
      (apply growl (growl-message results))))

(defn new-growl-reporter []
    (GrowlReporter.))
