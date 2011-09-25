(ns speclj.report.growl
  (:use
    [speclj.exec :only (pass? fail? pending?)]
    [speclj.report.documentation :only (new-documentation-reporter)]
    [speclj.reporting :only (report-fail report-description report-message report-pass report-pending report-runs)]
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
  (let [result-map (categorize results)
        tally (apply hash-map (interleave (keys result-map) (map count (vals result-map))))
        title (if (zero? (:fail tally)) "Success" "Failure")]
    ["Message" title (describe-counts-for result-map)]))

(defn tally [result-map]
  (apply hash-map (interleave (keys result-map) (map count (vals result-map)))))

(deftype GrowlReporter [passes fails results]
    Reporter
    (report-message [this message]
      (report-message (new-documentation-reporter) message))
    (report-description [this description]
      (report-description (new-documentation-reporter) description))
    (report-pass [this result]
      (report-pass (new-documentation-reporter) result))
    (report-pending [this result]
      (report-pending (new-documentation-reporter) result))
    (report-fail [this result]
      (report-fail (new-documentation-reporter) result))
    (report-runs [this results]
      (report-runs (new-documentation-reporter) results)
      (apply growl (growl-message results))))

(defn new-growl-reporter []
    (GrowlReporter. (atom 0) (atom 0) (atom nil)))
