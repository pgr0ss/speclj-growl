(ns speclj.report.growl_spec
  (:use
    [speclj.core]
    [speclj.exec :only (pass-result fail-result pending-result)]
    [speclj.report.growl]
    [speclj.reporting :only (report-runs)]))


(describe "Growl Reporter"

  (with reporter (new-growl-reporter))
  (with notification (atom nil))
  (with title (atom nil))
  (with message (atom nil))
  (with fake-growl (fn [_notification _title _message]
                     (reset! @notification _notification)
                     (reset! @title _title)
                     (reset! @message _message)))


  (it "growls empty test summary"
      (binding [growl @fake-growl]
        (report-runs @reporter [])
        (should= "Success" @@notification)
        (should= "No Specs" @@title)
        (should= "No Specs" @@message)))

  (it "growls a successful run"
      (pending)
      (binding [growl @fake-growl]
        (let [result1 (pass-result nil 0.1)
              result2 (pass-result nil 0.02)
              results [result1 result2]]
          (report-runs @reporter results)
          (should= "Success" @@notification)
          (should= "Success" @@title)
          (should= "2 examples, 0 failures" @@message)))))
