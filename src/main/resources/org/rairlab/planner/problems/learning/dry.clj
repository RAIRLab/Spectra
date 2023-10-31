{:name       "learning"
 :background [(iff dry (not wet))
              (iff umbrella (not coat))


              (if was-outside (if (and rain (not umbrella)) wet))
              (if was-outside (if (and snow (not coat)) wet))]
 :start      [ rain
               (at a)
               (available b)
               ]

 :actions    [(define-action walkFromTo [?start ?end]
                {:preconditions [(at ?start)
                                 (available ?end)
                                ]
                 :additions     [(at ?end)
                                 was-outside]
                 :deletions     [(at ?start)]})

              (define-action useUmbrella []
                {:preconditions [rain]
                 :additions     [umbrella]
                 :deletions     [(not umbrella)]})


              (define-action useCoat []
                {:preconditions [snow]
                 :additions     [coat]
                 :deletions     [(not coat)]})

              ]


 :goal [(at b)
        dry]
 }