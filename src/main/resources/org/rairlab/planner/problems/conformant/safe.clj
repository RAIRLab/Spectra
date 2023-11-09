; Original problem from Joerg Hoffmann and Ronen Brafman
{:name "Safe"
    :background [ ]

    :actions [
        (define-action try [?x] {
            ; NOTE: Currently need a precondition otherwise question-answering portion doesn't work.
            :preconditions [ (combination ?x) ]
            :additions [ (if (right-combination ?x) (safe-open))]
            :deletions [ ]
        })
    ]
    :start [
        ; Unknown facts don't need to be stated 
        ; since we don't assume closed world assumption.
        ; (unknown (right-combination c1))
        ; (unknown (right-combination c2))
        ; (unknown (right-combination c3))
        ; (unknown (right-combination c4))
        ; (unknown (right-combination c5))

        (combination c1)
        (combination c2)
        (combination c3)
        (combination c4)
        (combination c5)

        ; One-of possibilities
        (or
            (and
                (right-combination c1)
                (not (right-combination c2))
                (not (right-combination c3))
                (not (right-combination c4))
                (not (right-combination c5))
            )

            (and
                (not (right-combination c1))
                (right-combination c2)
                (not (right-combination c3))
                (not (right-combination c4))
                (not (right-combination c5))
            )

            (and
                (not (right-combination c1))
                (not (right-combination c2))
                (right-combination c3)
                (not (right-combination c4))
                (not (right-combination c5))
            )

            (and
                (not (right-combination c1))
                (not (right-combination c2))
                (not (right-combination c3))
                (right-combination c4)
                (not (right-combination c5))
            )

            (and
                (not (right-combination c1))
                (not (right-combination c2))
                (not (right-combination c3))
                (not (right-combination c4))
                (right-combination c5)
            )

        )

    ]
    :goal [ (safe-open) ]

}






