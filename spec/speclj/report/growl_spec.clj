(ns speclj.report.growl_spec
  (:require [speclj.components :refer [new-characteristic new-description]]
            [speclj.reporting :refer [report-runs report-error]]
            [speclj.platform :refer [new-exception new-failure new-pending]]
            [speclj.results :refer [pass-result fail-result pending-result error-result]]
            [speclj.core :refer :all]
            [speclj.run.standard :refer [run-specs]]
            [speclj.report.growl :as g]))


(describe "Growl Reporter"
  (with reporter (g/new-growl-reporter))
  (with result (atom nil))
  (with message (atom nil))

  (around [it]
    (with-redefs [g/growl (fn [_result _message]
                            (reset! @result _result)
                            (reset! @message _message))]
      (it)))

  (describe "report-runs"

    (it "growls summary information for no test runs"
      (report-runs @reporter [])
      (let [output (with-out-str (report-runs @reporter []))]
        (should= :pass @@result)
        (should= "0 examples, 0 failures\nTook 0.00000 seconds" @@message)))

    (it "growls a successful run"
      (let [results [(pass-result nil 0.1) (pass-result nil 0.02)]
            output (with-out-str (report-runs @reporter results))]
        (should= :pass @@result)
        (should= "2 examples, 0 failures\nTook 0.12000 seconds" @@message)))

    (it "growls an unsuccessful run"
      (let [results [(pass-result nil 0.1)
                     (fail-result nil 2 (new-failure "blah"))]
            output (with-out-str (report-runs @reporter results))]
        (should= :fail @@result)
        (should= "2 examples, 1 failures\nTook 2.10000 seconds" @@message)))

    (it "growls a run with pending"
      (let [results [(pass-result nil 0.1)
                     (pending-result nil 1 (new-pending "blah"))]
            output (with-out-str (report-runs @reporter results))]
        (should= :pass @@result)
        (should= "2 examples, 0 failures, 1 pending\nTook 1.10000 seconds"
                 @@message)))

    (it "growls a run with failing and pending"
      (let [results [(pass-result nil 0.1)
                     (fail-result nil 1.1 (new-failure "blah"))
                     (pending-result nil 1 (new-pending "blah"))]
            output (with-out-str (report-runs @reporter results))]
        (should= :fail @@result)
        (should= "3 examples, 1 failures, 1 pending\nTook 2.20000 seconds"
                 @@message)))

    (it "growls compilation errors"
      (let [error (error-result (new-exception "Failed to compile"))
            output (with-out-str (report-error @reporter error))]
        (should= :error @@result)
        (should= "Exception: Failed to compile" @@message)))))
