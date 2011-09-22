(ns speclj.report.growl
  (:use
    [speclj.reporting :only ()]
    [speclj.report.progress :only (print-summary)]
    [clj-growl.core :only (make-growler)])
  (:import
    [speclj.reporting Reporter]))

(def growl (make-growler "" "speclj" ["Success" true "Failure" true]))

(deftype GrowlReporter [passes fails results]
    Reporter
    (report-message [this message])
    (report-description [this description])
    (report-pass [this result])
    (report-pending [this result])
    (report-fail [this result])
    (report-runs [this results]
      (if (seq results)
        (growl "Success" "Success" (with-out-str (print-summary results)))
        (growl "Success" "No Specs" "No Specs"))))

(defn new-growl-reporter []
    (GrowlReporter. (atom 0) (atom 0) (atom nil)))
