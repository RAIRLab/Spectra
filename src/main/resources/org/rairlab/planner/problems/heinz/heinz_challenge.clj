{:definitions
        {:name       "Heinz Dilemma"
         :background [(cures radium-drug-x cancerx)]
         :start      [(not steal)


                      (collateral income)



                      (sick (wife heinz) cancerx)


                      (= 5000 (cost radium-drug-x))
                      (not (can-spend 5000))
                      (not (possess radium-drug-x))]

          :actions

                     [(define-action administer-medicine [?medicine ?condition ?person]
                                     {:preconditions [(sick ?person ?condition)
                                                      (cures ?medicine ?condition)
                                                      (possess ?medicine)]
                                      :additions     [(healthy ?person)]
                                      :deletions     [(sick ?person ?condition)]})

                      (define-action buy-medicine [?medicine]
                                     {:preconditions [(not (possess ?medicine))
                                                      (can-spend (cost ?medicine))]
                                      :additions     [(possess ?medicine)]
                                      :deletions     [(not (possess ?medicine))]})

                      (define-action steal-medicine [?medicine]
                                     {:preconditions [(not (can-spend (cost ?medicine)))
                                                      (not (possess ?medicine))]
                                      :additions     [(possess ?medicine)
                                                      steal]
                                      :deletions     [(not steal)
                                                      (not (possess ?medicine))]})

                      (define-action apply-loan [?amount]
                                     {:preconditions [  (not (can-spend ?amount))]
                                      :additions     [(approved (loan ?amount))]
                                      :deletions     []})

                      (define-action finance [?amount ?with-collateral]
                                     {:preconditions [(collateral ?with-collateral)
                                                      (approved (loan ?amount)) (not (can-spend ?amount))]
                                      :additions     [(can-spend ?amount)
                                                      have-loan]
                                      :deletions     [(not (can-spend ?amount))]})]}

 :goals {G1 {:priority    1.0
             :description "Don't steal."
             :state       [(not steal)]}


         G2 {:priority    2.0
             :description "My wife should be healthy"
             :state       [(healthy (wife heinz))]}}}
