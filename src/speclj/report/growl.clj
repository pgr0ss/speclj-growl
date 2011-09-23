(ns speclj.report.growl
  (:use
    [speclj.exec :only (pass? fail? pending?)]
    [speclj.reporting :only ()]
    [speclj.report.progress :only (print-summary)]
    [clj-growl.core :only (make-growler)])
  (:import
    [speclj.reporting Reporter]))

(def growl (make-growler "" "speclj" ["Message" true]))

(defn- categorize [results]
  (reduce (fn [tally result]
    (cond (pending? result) (update-in tally [:pending] conj result)
      (fail? result) (update-in tally [:fail] conj result)
      :else (update-in tally [:pass] conj result)))
    {:pending [] :fail [] :pass []}
    results))

(defn- describe-counts-for [result-map]
  (let [tally (apply hash-map (interleave (keys result-map) (map count (vals result-map))))
        always-on-counts [(str (apply + (vals tally)) " examples")
                          (str (:fail tally) " failures")]]
    (apply str
      (interpose ", "
        (if (> (:pending tally) 0)
          (conj always-on-counts (str (:pending tally) " pending"))
          always-on-counts)))))

(defn growl-message [results]
  (let [result-map (categorize results)]
    (describe-counts-for result-map)))

(deftype GrowlReporter [passes fails results]
    Reporter
    (report-message [this message])
    (report-description [this description])
    (report-pass [this result])
    (report-pending [this result])
    (report-fail [this result])
    (report-runs [this results]
      (print-summary results)
      (growl "Message" "Specs" (growl-message results))))

(defn new-growl-reporter []
    (GrowlReporter. (atom 0) (atom 0) (atom nil)))
