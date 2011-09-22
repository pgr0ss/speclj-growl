(ns speclj.report.growl
  (:use
    [speclj.reporting :only ()]
    [clj-growl.core :only (make-growler)])
  (:import
    [speclj.reporting Reporter]))

(def growl (make-growler "" "speclj" ["Message" true]))

(deftype GrowlReporter [passes fails results]
    Reporter
    (report-message [this message])
    (report-description [this description])
    (report-pass [this result])
    (report-pending [this result] (growl "Message" "Specs" (str result)))
    (report-fail [this result])
    (report-runs [this results]))

(defn new-growl-reporter []
    (GrowlReporter. (atom 0) (atom 0) (atom nil)))
