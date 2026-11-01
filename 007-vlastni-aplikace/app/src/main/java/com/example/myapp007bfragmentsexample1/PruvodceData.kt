package com.example.myapp007bfragmentsexample1

// Všechna data aplikace na jednom místě.
// "object" znamená, že existuje jen jedna instance a dostaneš se k ní odkudkoli: PruvodceData.mista
object PruvodceData {

    val mista = listOf(
        Misto(
            id = 1,
            nazev = "Kittelův dům (Burk)",
            lokalita = "Krásná",
            obrazek = R.drawable.misto_kitteluv_dum,
            popis = "Mohutná čtyřpodlažní roubenka z roku 1747, které místní říkají Burk. " +
                    "Doktor Kittel ji nepostavil jen jako svůj domov, ale hlavně jako horské " +
                    "sanatorium. Měl tu lékárnu, pokoje pro pacienty, pitevnu i knihovnu. " +
                    "Kolem roku 2004 hrozilo domu zřícení, obec Pěnčín ho koupila a patnáct let " +
                    "opravovala. Rekonstrukce skončila v roce 2019 a dnes je tu muzeum.",
            zajimavost = "Dům patří mezi nejvyšší roubené stavby ve střední Evropě a zachovala " +
                    "se v něm patrová černá kuchyně, jaké už na severu Čech skoro nenajdete.",
            povest = "Lidé si šeptali, že tak velký dům a takové bohatství nemůže mít obyčejný " +
                    "lékař. Prý mu pomáhal sám ďábel a v domě se po nocích dělo leccos tajemného.",
            hledatVMapach = "Kittelův dům, Krásná, Pěnčín"
        ),
        Misto(
            id = 2,
            nazev = "Kostel sv. Josefa",
            lokalita = "Krásná",
            obrazek = R.drawable.misto_kostel,
            popis = "Barokní kostel stojí jen kousek od Kittelova domu a je nejvýznamnější " +
                    "památkou celé obce. Stavělo se na něm v letech 1756 až 1760.",
            zajimavost = "Věž dostal kostel až o necelých dvacet let později, v roce 1779.",
            povest = null,
            hledatVMapach = "Kostel sv. Josefa, Krásná, Pěnčín"
        ),
        Misto(
            id = 3,
            nazev = "Studánka sv. Josefa",
            lokalita = "Krásná",
            obrazek = R.drawable.misto_studanka,
            popis = "Studánka u kostela, asi 130 metrů od Kittelova domu. Voda z okolních " +
                    "studánek hrála v Kittelově léčení hlavní roli. Pomocníci pacienty polévali " +
                    "nebo jim nořili bolavé části těla přímo do pramene.",
            zajimavost = "Kittel léčil vodou o desítky let dřív než slavní Priessnitz a Kneipp. " +
                    "Pozor při návštěvě: schody vedou přímo do vody.",
            povest = null,
            hledatVMapach = "Studánka sv. Josefa, Krásná, Pěnčín"
        ),
        Misto(
            id = 4,
            nazev = "Čertovy kameny",
            lokalita = "okolí Krásné",
            obrazek = R.drawable.misto_certovy_kameny,
            popis = "Skalní místo v okolí Krásné, které je spojené s nejznámější pověstí " +
                    "o doktoru Kittelovi. Leží na trase vycházek po stopách tajemného doktora.",
            zajimavost = "Kittelovi se kvůli pověstem dodnes přezdívá jizerskohorský Faust.",
            povest = "Právě tady se měl mladý Kittel setkat s ďáblem a upsat se mu. Za svou duši " +
                    "dostal létající plášť a zázračnou bylinu všehoj. Smlouva prý platila " +
                    "čtyřicet let.",
            hledatVMapach = "Krásná, Pěnčín"
        ),
        Misto(
            id = 6,
            nazev = "Rozhledna Černá Studnice",
            lokalita = "Černostudniční hřbet",
            obrazek = R.drawable.misto_rozhledna,
            popis = "Kamenná rozhledna vysoká 26 metrů, postavená ze žulových kvádrů podle plánů " +
                    "architekta Roberta Hemmricha. Otevřena byla 14. srpna 1905. U rozhledny je " +
                    "restaurace, dětské hřiště, zookoutek, skalní vyhlídka a malé arboretum.",
            zajimavost = "Spodní zdi rozhledny jsou silné až 160 cm. Patří k nejmohutnějším " +
                    "kamenným rozhlednám v Jizerských horách.",
            povest = null,
            hledatVMapach = "Rozhledna Černá Studnice"
        ),
        Misto(
            id = 7,
            nazev = "Farma Pěnčín",
            lokalita = "Pěnčín",
            obrazek = R.drawable.misto_farma,
            popis = "Ekofarma s kozami a sýrárnou, zemědělským muzeem a ukázkou výroby skleněných " +
                    "perlí. Z farmy jezdí vyhlídkový vláček do Kittelova areálu v Krásné " +
                    "a dál na Horní Černou Studnici.",
            zajimavost = "V roce 2012 získala farma titul Farma roku.",
            povest = null,
            hledatVMapach = "Farma Pěnčín"
        ),
        Misto(
            id = 8,
            nazev = "Pravěké doly na Jistebsku",
            lokalita = "Jistebsko",
            obrazek = R.drawable.misto_jistebsko,
            popis = "Asi před 7 000 lety (5400–4800 př. n. l.) se tu těžil velmi tvrdý kámen, " +
                    "kterému archeologové říkají metabazit. Pravěcí lidé z něj vyráběli sekerky. " +
                    "V krajině se dodnes dochovaly stovky těžebních jam.",
            zajimavost = "Sekerky z Jistebska se dostaly až na Moravu, do Polska, Německa, " +
                    "Rakouska, Maďarska i Nizozemí. Kittelův dům je postavený i z tohoto kamene " +
                    "a pravěké sekerky jsou k vidění v jeho muzeu.",
            povest = null,
            hledatVMapach = "Jistebsko, Pěnčín"
        )
    )

    fun najdiMisto(id: Int): Misto? = mista.find { it.id == id }

    // Pověsti pro záložku Pověsti: dvojice (nadpis, text)
    val povesti = listOf(
        "Smlouva s ďáblem" to
            "Kittel prý zbohatl tak rychle, že to lidem nedávalo smysl. Začalo se povídat, " +
            "že se na Čertových kamenech upsal ďáblu a ten mu výměnou slíbil úspěch a slávu.",
        "Létající plášť" to
            "Od ďábla měl dostat kouzelný plášť. Stačilo se do něj zahalit a za chvíli byl " +
            "u pacienta ve Vídni nebo v Praze.",
        "Bylina všehoj" to
            "Druhým darem byla bylina všehoj, která podle pověsti vyléčila úplně každou nemoc. " +
            "Proto prý Kittel dokázal pomoci i tam, kde jiní lékaři selhali.",
        "Smržovský grimoár" to
            "Svá kouzla měl Kittel čerpat z tajemné čarodějné knihy, které se říká Smržovský grimoár.",
        "Kostra od Žernovníku" to
            "V pitevně měl Kittel lidskou kostru. Podle pověsti patřila neznámému vojákovi, " +
            "kterého našli u pramene Žernovníku.",
        "Konec smlouvy" to
            "Smlouva s ďáblem prý platila čtyřicet let. Podle jedné verze si pro doktora pekelník " +
            "nakonec opravdu přišel."
    )

    // Otázky kvízu "Pravda, nebo pověst?"
    data class Otazka(val tvrzeni: String, val jePravda: Boolean, val vysvetleni: String)

    val kviz = listOf(
        Otazka("Kittel léčil pacienty vodou dřív než Priessnitz a Kneipp.", true,
            "Je to pravda. Vodoléčbu prováděl o desítky let dřív."),
        Otazka("Kittel létal k pacientům do Vídně na kouzelném plášti.", false,
            "Je to pověst. Plášť mu měl dát ďábel."),
        Otazka("Kittelův dům patří mezi nejvyšší roubené stavby ve střední Evropě.", true,
            "Je to pravda. Má čtyři podlaží."),
        Otazka("Bylina všehoj dokázala vyléčit každou nemoc.", false,
            "Je to pověst. Bylinu prý dostal od ďábla."),
        Otazka("Rozhledna na Černé Studnici byla otevřena v roce 1905.", true,
            "Je to pravda. Otevřeli ji 14. srpna 1905."),
        Otazka("Na Čertových kamenech se Kittel upsal ďáblu.", false,
            "Je to pověst, i když místo samotné opravdu existuje."),
        Otazka("Z Jistebska se v pravěku dostávaly kamenné sekerky do půlky Evropy.", true,
            "Je to pravda. Našly se třeba v Německu nebo v Nizozemí."),
        Otazka("Doktor Kittel měl od ďábla smlouvu na čtyřicet let.", false,
            "Je to pověst. Vypráví se o ní na Čertových kamenech."),
        Otazka("Obec Pěnčín zachránila Kittelův dům, který hrozil zřícením.", true,
            "Je to pravda. Rekonstrukce trvala 15 let a skončila v roce 2019.")
    )
}
