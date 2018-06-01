{:name       "learning"
 :background []
 :start      [(= c1 (clause a null))
              (= c2 (clause (not a) b))
              (= c3 (clause c (not b)))
              (= c3 (clause (not c) null))
              ]

 :actions    [(define-action resolve1a [?c1 ?c2 ?u ?w]
                {:preconditions [(= ?c1 (clause ?u null))
                                 (= ?c2 (clause (not ?u) ?w))]
                 :additions     [(= (comb ?c1 ?c2) (clause null ?w))]
                 :deletions     []})


              (define-action resolve1b [?c1 ?c2 ?u ?w]
                {:preconditions [(= ?c1 (clause null ?u) )
                                 (= ?c2 (clause ?w (not ?u)) )]
                 :additions     [(= (comb ?c1 ?c2)  (clause ?w null))]
                 :deletions     []})
              ]


 :goal [(= ?id (clause null null))]
 }
