(ns speclj.report.growl_spec
  (:use
    [speclj.core]
    [speclj.components :only (new-characteristic new-description)]
    [speclj.results :only (pass-result fail-result)]
    [speclj.report.growl]
    [speclj.reporting :only (report-runs)])
  (:import
    [speclj SpecFailure SpecPending]))


(describe "Growl Reporter"
  (with reporter (new-growl-reporter))
  (with notification (atom nil))
  (with title (atom nil))
  (with message (atom nil))
  (with fake-growl (fn [_notification _title _message]
                     (reset! @notification _notification)
                     (reset! @title _title)
                     (reset! @message _message)))

  (describe "report-runs"

    (it "growls summary information for no test runs"
      (binding [growl @fake-growl]
        (let [output (with-out-str (report-runs @reporter []))]
          (should= "Message" @@notification)
          (should= "Success" @@title)
          (should= "Finished in 0.00000 seconds\n0 examples, 0 failures" @@message))))

    (it "growls a successful run"
      (binding [growl @fake-growl]
        (let [result1 (pass-result nil 0.1)
              result2 (pass-result nil 0.02)
              results [result1 result2]
              output (with-out-str (report-runs @reporter results))]
          (should= "Message" @@notification)
          (should= "Success" @@title)
          (should= "Finished in 0.12000 seconds\n2 examples, 0 failures" @@message))))

    (it "growls an unsuccessful run"
      (binding [growl @fake-growl]
        (let [result1 (pass-result nil 0.1)
              description (new-description "Desc" *ns*)
              characteristic (new-characteristic "says fail" description "fail")
              result2 (fail-result characteristic 2 (SpecFailure. "blah"))
              results [result1 result2]
              output (with-out-str (report-runs @reporter results))]
          (should= "Message" @@notification)
          (should= "Failure" @@title)
          (should= "Finished in 2.10000 seconds\n2 examples, 1 failures" @@message))))))

(run-specs)
