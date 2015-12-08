def spamicity_of_given_word(good, bad, word, ngood, nbad):
    g = 0.0
    b = 0.0
    if word in good:
        g = float(2 * good[word])
    if word in bad:
        b = float(bad[word])


    if (g+b >= 5):
        print ("ngood : " + str(ngood) + "      nbad : " + str(nbad))
        x = b/nbad
        y = g/ngood
        print (" b : "+ str(b) + " g : " + str(g))
        print (" x : " + str(x) + " y : " + str(y))
        a = min(1, x)/min(1, y + min(1, x))
        returnVal = max(0.01, min (0.99, a))
        return returnVal
    else:
        return 0.4
