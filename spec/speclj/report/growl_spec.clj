(ns speclj.report.growl_spec
  (:use
    [speclj.core]
    [speclj.components :only (new-characteristic new-description)]
    [speclj.results :only (pass-result fail-result)]
    [speclj.report.growl]
    [speclj.reporting :only (report-runs report-error)])
  (:import
    [speclj SpecFailure SpecPending]))


(describe "Growl Reporter"
  (with reporter (new-growl-reporter))
  (with result (atom nil))
  (with message (atom nil))
  (with fake-growl (fn [_result _message]
                     (reset! @result _result)
                     (reset! @message _message)))

  (describe "report-runs"

    (it "growls summary information for no test runs"
      (with-redefs [growl @fake-growl]
        (let [output (with-out-str (report-runs @reporter []))]
          (should= :pass @@result)
          (should= "0 examples, 0 failures\nTook 0.00000 seconds" @@message))))

    (it "growls a successful run"
      (with-redefs [growl @fake-growl]
        (let [result1 (pass-result nil 0.1)
              result2 (pass-result nil 0.02)
              results [result1 result2]
              output (with-out-str (report-runs @reporter results))]
          (should= :pass @@result)
          (should= "2 examples, 0 failures\nTook 0.12000 seconds" @@message))))

    (it "growls an unsuccessful run"
      (with-redefs [growl @fake-growl]
        (let [result1 (pass-result nil 0.1)
              description (new-description "Desc" *ns*)
              characteristic (new-characteristic "says fail" description "fail")
              result2 (fail-result characteristic 2 (SpecFailure. "blah"))
              results [result1 result2]
              output (with-out-str (report-runs @reporter results))]
          (should= :fail @@result)
          (should= "2 examples, 1 failures\nTook 2.10000 seconds" @@message))))

    (it "growls compilation errors"
      (with-redefs [growl @fake-growl]
        (let [ex (Exception. "Failed to compile")
              output (with-out-str (report-error @reporter ex))]
          (should= :error @@result)
          (should= "Exception: Failed to compile" @@message))))))

(run-specs)
