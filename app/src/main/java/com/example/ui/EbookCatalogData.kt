package com.example.ui

import androidx.compose.ui.graphics.Color

object EbookCatalogData {

    val BOOKS_PART1: List<EbookDefinition> = listOf(
        EbookDefinition(
            id = "ebook_tour_guide",
            number = "No. 1",
            title = "The Artist's Tour Guide",
            subtitle = "Live Operations, Crew Logistics & Road Revenue",
            price = 25000.0,
            coverGradient = listOf(Color(0xFF4A0E17), Color(0xFF1F060A), Color(0xFF0D0204)),
            accentColor = Color(0xFFFFD54F),
            chapters = listOf(
                EbookChapter(
                    "Before You Launch",
                    "A tour is more than booking a few shows. You are building a temporary live operation around your music.\n\nCreate your tour with:\n• Tour name and banner — use artwork from your Camera or gallery.\n• Crew — hire the people you can afford.\n• Opening/closing acts — bring other artists onto the tour if you can afford them.\n• Setlist — choose from your released songs and albums.\n• Tour type — Club Run, City Tour, National Tour, or Global Tour.\n• Merchandise — select products from your Merch catalog.\n• Tickets — set General, VIP, and Platinum prices.\n• Launch date — choose how much time you want to build anticipation."
                ),
                EbookChapter(
                    "Your Crew Matters",
                    "Every crew member has a job.\n\nTour Manager helps control logistics and revenue.\nHead of Security protects the show and helps prevent incidents.\nAudio Engineer improves the live sound and audience experience.\nWardrobe Stylist handles your stage appearance.\nInstrumentalists strengthen your live performances.\nDancers add choreography and visual energy.\nPhotographer/Videographer creates tour content.\nMedics help manage physical and vocal strain.\nDriver helps keep transportation running smoothly.\n\nYou don't need everyone. For example, you might start a small club tour with an audio engineer, security and driver. A major global tour may justify a much larger crew."
                ),
                EbookChapter(
                    "Pick Your Scale",
                    "The larger the tour, the bigger the operation.\n\n• Club Run — smaller venues and fewer cities ($1 – $6 ticket range).\n• City Tour — a larger regional run ($5 – $18 ticket range).\n• National Tour — a major domestic operation ($20 – $50 ticket range).\n• Global Tour — the biggest scale, with more cities, people and logistical demands ($50 – $150 ticket range).\n\nDon't build a global tour just because you can afford the ticket. Make sure your career and Artist Level can support it."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_business_works",
            number = "No. 2",
            title = "Build a Business That Actually Works",
            subtitle = "Cashflow, Scalability, WeShare IPOs & Enterprise Survival",
            price = 50000.0,
            coverGradient = listOf(Color(0xFF0A3B24), Color(0xFF041C11), Color(0xFF020E08)),
            accentColor = Color(0xFF00E676),
            chapters = listOf(
                EbookChapter(
                    "Your Business Has Two Sides",
                    "Starting a business is not supposed to mean: Put in money → receive a percentage every week.\n\nYour business should actually operate.\n\nEvery venture has:\nMoney coming in — customers, sales, bookings and other revenue.\nMoney going out — staff, maintenance, supplies, licences and other operating costs.\n\nWhat remains is your weekly net cashflow.\n\nExample:\nA taxi business makes $680 this week. Operating costs are $440. Your business keeps $240 net profit.\n\nIf you want to earn more, you need to grow the operation — not magically increase the percentage return on your original investment."
                ),
                EbookChapter(
                    "Going Public on WeShare",
                    "You can attempt to list part of an eligible business on WeShare.\n\nMaking money for one week isn't enough. A business must establish a real track record and pass screening.\n\nThe game requires an eligible business to have operated for roughly 30–40 weeks, maintain healthy performance, have good customer retention and have no outstanding business loan.\n\nThe screening costs $20,000 and takes about two weeks.\n\nIf approved, you'll receive important information such as share price, number of shares, percentage being offered, and other listing details."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_equity_control",
            number = "No. 3",
            title = "Owning, Selling & Sharing a Business",
            subtitle = "Valuation, Investor Equity, Control Dynamics & Scale",
            price = 75000.0,
            coverGradient = listOf(Color(0xFF28104E), Color(0xFF14072B), Color(0xFF090214)),
            accentColor = Color(0xFFC084FC),
            chapters = listOf(
                EbookChapter(
                    "Losing Control",
                    "Owning the largest percentage is important because control follows ownership.\n\nIf your ownership falls below 50%, you lose managerial control of that venture.\n\nWhen player is no longer in control of a business, they can't make any changes to that business like hiring/firing a staff, upgrading business, marketing, etc.\n\nThe majority shareholder runs the business, and all the player will see is the profit and changes made by the new owner.\n\nSome owners may run the business down through cost cuts, while others make it better through expansions.\n\nCrucially, the player can sell their remaining shares at any time for cash!"
                ),
                EbookChapter(
                    "Business Managers & Bottlenecks",
                    "A business manager does more than collect a salary.\n\nWhen you control a venture, your manager alerts you to exact operational bottlenecks:\n“Boss, we have 120 recurring customers, but our food truck has no seating! We need to upgrade to a Diner and hire 2 more Chefs and 2 more Waiters.”\n\nThese messages give you actionable clarity on exactly which staff and facility upgrades are needed."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_crypto_empire",
            number = "No. 4",
            title = "Building Your Crypto Empire",
            subtitle = "Tokenomics, Genesis Markets, Liquidity & Web3 Systems",
            price = 100000.0,
            coverGradient = listOf(Color(0xFF0F2042), Color(0xFF081226), Color(0xFF030712)),
            accentColor = Color(0xFF38BDF8),
            chapters = listOf(
                EbookChapter(
                    "The Web3 Enterprise Loop",
                    "A crypto business works differently from an ordinary venture. You are building a project, attracting users, generating protocol fees, and eventually creating a token that can be traded on Genesis Markets.\n\nThe five core pillars are Developers, Security Audits, Platform Tech, Marketing, and Liquidity Pools."
                ),
                EbookChapter(
                    "Defending Against Hacks",
                    "If your security tier is low while liquidity pools exceed $50K, flash loan exploiters will drain smart contracts. Invest in senior Solidity auditors before scaling your circulating market cap."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_farming_masterclass",
            number = "No. 5",
            title = "The Agronomist's Masterclass: Demystifying Farmland",
            subtitle = "Soil Fertility, Weather Defense, Silo Economics & Industrial Agribusiness",
            price = 30000.0,
            coverGradient = listOf(Color(0xFF1B3B10), Color(0xFF0E2208), Color(0xFF050F03)),
            accentColor = Color(0xFF4ADE80),
            chapters = listOf(
                EbookChapter(
                    "Farming Simplified in 3 Steps",
                    "Many entrepreneurs find agriculture intimidating, but it follows a clear three-step cycle:\n\n1. Land & Planting: Acquire land (leasing has low upfront cost; buying gives $0 rent and land appreciation). Select crops in the Crops tab and plant available acres.\n\n2. Field Care & Workforce: Every 10 cultivated acres requires 1 dedicated Farmhand. Understaffed fields suffer 30% yield loss from unchecked weeds and pests.\n\n3. Harvesting & Cashout: When crops reach their maturity week, click 'Harvest into Silo' and 'Sell All to Market' for immediate lump-sum revenue!"
                ),
                EbookChapter(
                    "Weather Defense & Machinery",
                    "Mother Nature can wipe out unshielded crops:\n• Droughts (-30% yield): Protected by Automated Irrigation Systems.\n• Torrential Floods (-15% yield): Protected by Combine Harvesters.\n• Locust & Pest Swarms (-25% yield): Protected by Chemical Sprayers.\n\nInvesting in proper machinery turns high-risk farming into a dependable, weather-proof cash cow."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_restaurant_empire",
            number = "No. 6",
            title = "Culinary Architecture: Food Truck to Fine Dining",
            subtitle = "Table Turnover, Waiter Logistics, Chef Hierarchy & Kitchen Equipment",
            price = 35000.0,
            coverGradient = listOf(Color(0xFF4A1808), Color(0xFF260B04), Color(0xFF120401)),
            accentColor = Color(0xFFFB923C),
            chapters = listOf(
                EbookChapter(
                    "The Seating Overcrowding Dilemma",
                    "A common mistake is keeping a Food Truck when recurring customers surpass 100.\n\nA Food Truck has zero seating! When 100+ diners show up, they expect tables, chairs, and ambient dining. If they have nowhere to sit, dozens will leave in frustration every single week.\n\nThe more recurring customers you attract, the higher premises tier you need (Food Truck → Casual Diner → Bistro → Fine Dining). Upgrading unlocks seating capacity and stops customer churn."
                ),
                EbookChapter(
                    "Balancing Chefs, Waiters & Cleaners",
                    "Every dining room operates on tight ratios:\n• Chefs: Need 1 Chef per 25 recurring diners.\n• Senior Chef: 1 required per 4 chefs to direct kitchen line prep.\n• Waiters: Need 1 Waiter per 20 diners. If waiters are missing, cooked food sits on the pass and spoils without earning revenue!\n• Cleaners: Need 1 Cleaner per 20 diners to maintain sanitation and prevent hygiene health fines."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_taxi_operations",
            number = "No. 7",
            title = "The Urban Transit Playbook: Taxi Fleet Realism",
            subtitle = "Mileage Depreciation, Mechanic Ratios, Depot Scaling & SCC Compliance",
            price = 28000.0,
            coverGradient = listOf(Color(0xFF423206), Color(0xFF211903), Color(0xFF0D0A01)),
            accentColor = Color(0xFFFACC15),
            chapters = listOf(
                EbookChapter(
                    "The 1-to-4 Mechanic Golden Rule",
                    "In fleet logistics, every 4 taxis requires at least 1 full-time fleet mechanic. Without mechanics, vehicles lose 3% road condition every week. When a taxi hits 10% condition, it suffered catastrophic breakdown and is TOTALED and destroyed. Always maintain 1 mechanic per 4 vehicles."
                ),
                EbookChapter(
                    "Depot Capacity & Drivers",
                    "Never buy more cabs than your drivers can operate. Each idle taxi incurs commercial parking taxes and municipal fleet upkeep without earning fares. When your fleet reaches depot capacity, upgrade the depot facility before expanding further."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_hotel_hospitality",
            number = "No. 8",
            title = "Hospitality Architecture: High-End Hotel Management",
            subtitle = "Room Furnishing, Housekeeping Metrics, VIP Amenities & Peak Seasons",
            price = 65000.0,
            coverGradient = listOf(Color(0xFF132A4A), Color(0xFF091626), Color(0xFF030812)),
            accentColor = Color(0xFF60A5FA),
            chapters = listOf(
                EbookChapter(
                    "Housekeeping & Hygiene Ratings",
                    "Hotel profitability hinges on guest review scores. You need 1 housekeeper per 4 furnished rooms. If housekeeping falls behind, cleanliness drops below 70%, triggering guest cancellations and slashing occupancy."
                ),
                EbookChapter(
                    "High-Margin Amenities",
                    "Furnishing suites generates steady lodging income, but adding Rooftop Lounges, Spas, and Michelin Dining generates high-margin auxiliary revenues that can exceed room rates during peak tourist seasons."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_songwriting_depth",
            number = "No. 9",
            title = "The Sovereign Songwriter: Hitmaking Psychology",
            subtitle = "Vocal Mastery, Lyrical Depth, Melody Science & Creative Discipline",
            price = 20000.0,
            coverGradient = listOf(Color(0xFF38104A), Color(0xFF1C0826), Color(0xFF0C0312)),
            accentColor = Color(0xFFE879F9),
            chapters = listOf(
                EbookChapter(
                    "The Anatomy of a Timeless Melody",
                    "Hits are not written by accident. A hit song combines emotional vulnerability, rhythmic tension, and a memorable melodic hook that listeners can hum after a single listen. High lyrical depth combined with vocal mastery above 80 creates multi-million stream catalog assets."
                ),
                EbookChapter(
                    "Catalog Compounding",
                    "One hit single pays bills for a season. A deep, high-quality catalog of 20+ released songs creates compounding royalty streams that support independent studio ownership and massive world tours."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_record_deals",
            number = "No. 10",
            title = "The Art of Negotiation: Record Deals & Master Rights",
            subtitle = "Royalty Splits, Advance Traps, 360 Contracts & Catalog Ownership",
            price = 45000.0,
            coverGradient = listOf(Color(0xFF3D1818), Color(0xFF1F0B0B), Color(0xFF0F0404)),
            accentColor = Color(0xFFF87171),
            chapters = listOf(
                EbookChapter(
                    "The Illusion of the Big Advance",
                    "A $500,000 label advance is not free money — it is a high-interest uncollateralized loan against your future music royalties. Labels recoup 100% of video budgets, touring support, and recording costs before paying you a single cent in net royalties."
                ),
                EbookChapter(
                    "Master Ownership & Independence",
                    "Owning your master recordings ensures you receive 100% of streaming royalties and sync licensing payouts forever. Use label distribution when necessary, but fight to retain master ownership rights."
                )
            )
        )
    )

    val BOOKS_PART2: List<EbookDefinition> = listOf(
        EbookDefinition(
            id = "ebook_cinematic_acting",
            number = "No. 11",
            title = "The Cinematic Pivot: Screen Acting & Box Office Acclaim",
            subtitle = "Breaking into Film, Audition Protocols, WGT Screenplays & Box Office Fame",
            price = 50000.0,
            coverGradient = listOf(Color(0xFF3B280A), Color(0xFF1E1404), Color(0xFF0F0901)),
            accentColor = Color(0xFFFBBF24),
            chapters = listOf(
                EbookChapter(
                    "WGT Pictures & Feature Film Casting",
                    "The transition from musical artist to cinematic star is one of the most lucrative pivots in entertainment. On the Who Is Got Talent (WGT) Official portal, artists can apply for feature movie acting roles for a $50,000 casting and evaluation fee."
                ),
                EbookChapter(
                    "The 6 Industry Stardom Benchmarks",
                    "Major casting directors evaluate 6 rigorous industry benchmarks:\n1. Fans > 70,000\n2. At least 5 songs with ≥ 1,000,000 streams\n3. Vocal skill ≥ 80\n4. Followers on Cast ≥ 50,000\n5. Strong relationships (≥60) with at least 10 Artists\n6. YouTube / MeTube subscribers ≥ 100,000.\n\nSatisfying all 6 awards the coveted Lead Protagonist Role ($100k-$200k payout + massive fame). Meeting 3-5 benchmarks awards a Major Supporting Role ($70k-$80k). Even with fewer benchmarks, productions will approve a Cameo Role ($25k-$30k)!"
                )
            )
        ),
        EbookDefinition(
            id = "ebook_living_consequence",
            number = "No. 12",
            title = "The Living Consequence: Ethics, Respect & Public Image",
            subtitle = "Why Scandals Destroy Careers, Rebuilding Trust & Long-Term Wisdom",
            price = 15000.0,
            coverGradient = listOf(Color(0xFF122C34), Color(0xFF08161A), Color(0xFF030B0D)),
            accentColor = Color(0xFF2DD4BF),
            chapters = listOf(
                EbookChapter(
                    "Context Over Rules",
                    "The entertainment industry is not a static math puzzle. Every action echoes through your community, peers, and corporate sponsors. Reckless controversies might spike short-term sensationalism but will destroy brand endorsements and corporate partnerships."
                ),
                EbookChapter(
                    "Compounding Integrity",
                    "Maintaining integrity above 80% unlocks elite partnerships and lifelong fan devotion that no viral gimmick can buy. Wisdom compounds silently over time."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_weshare_mastery",
            number = "No. 13",
            title = "The Master of Stock Markets: WeShare & Genesis Trading",
            subtitle = "Index Analysis, IPO Underwriting, Dividend Yields & Bear Market Defense",
            price = 80000.0,
            coverGradient = listOf(Color(0xFF0F3628), Color(0xFF071C14), Color(0xFF020E0A)),
            accentColor = Color(0xFF34D399),
            chapters = listOf(
                EbookChapter(
                    "The Public Markets Playbook",
                    "Listing equity on WeShare unlocks liquidity and prestige. But public companies are held to strict fiduciary accountability. Underperforming quarters depress share prices and invite activist shareholder bids."
                ),
                EbookChapter(
                    "Dividend Reinvestment & Portfolio Building",
                    "Use weekly profits from cash-flowing businesses to buy blue-chip stocks during economic recessions when prices are depressed, creating self-sustaining generational wealth."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_brand_magnet",
            number = "No. 14",
            title = "Brand Magnet: Attracting Global Endorsement Milestones",
            subtitle = "How to Catch the Eye of Elite Tycoons, David Temitope's Criteria & Integrity",
            price = 40000.0,
            coverGradient = listOf(Color(0xFF331640), Color(0xFF1A0A21), Color(0xFF0C0310)),
            accentColor = Color(0xFFA855F7),
            chapters = listOf(
                EbookChapter(
                    "The Illusion of Easy Brand Deals",
                    "Ordinary commercial sponsors seek vanity metrics. But elite visionary founders like David Temitope of Alphadominity will never partner with unproven or reckless artists. His fellowship ambassadorship is an impossible milestone reserved exclusively for cultural icons with unbroken integrity, multi-million hit catalogs, and massive community reverence."
                ),
                EbookChapter(
                    "Becoming Invaluable to Brands",
                    "Build authentic cultural influence rather than spamming followers. High respect and moral discipline make you a rare asset that luxury conglomerates will bid fortunes to align with."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_social_virality",
            number = "No. 15",
            title = "Social Media Warfare: Cast & MeTube Algorithms",
            subtitle = "Virality Science, Follower Conversion, Livestreams & Hater Management",
            price = 18000.0,
            coverGradient = listOf(Color(0xFF381525), Color(0xFF1D0912), Color(0xFF0E0408)),
            accentColor = Color(0xFFF43F5E),
            chapters = listOf(
                EbookChapter(
                    "Hook, Narrative, Retention",
                    "Social platforms reward emotional engagement. Behind-the-scenes studio sessions, genuine community interactions, and artistic transparency build loyal followers that convert directly into tour ticket buyers."
                ),
                EbookChapter(
                    "MeTube Monetization Loops",
                    "Producing high-concept visualizers and music videos drives subscriber growth past 100K, fulfilling critical prerequisites for major film casting and global brand deals."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_mental_fortitude",
            number = "No. 16",
            title = "Mental Fortitude: Overcoming Tour Exhaustion & Burnout",
            subtitle = "Stress Management, Vocal Rest, Mental Health & Therapy in Showbiz",
            price = 12000.0,
            coverGradient = listOf(Color(0xFF1A2438), Color(0xFF0C121E), Color(0xFF05080E)),
            accentColor = Color(0xFF38BDF8),
            chapters = listOf(
                EbookChapter(
                    "The Hidden Toll of Stardom",
                    "Relentless studio sessions, cross-country flights, and public scrutiny compound mental stress. When stress surpasses 80%, vocal pitch accuracy deteriorates and public interview blunders escalate."
                ),
                EbookChapter(
                    "Rest as a Strategic Asset",
                    "Taking periodic downtime, scheduling therapy, and delegating business operations to capable managers preserves creative vitality and prevents career-ending nervous breakdowns."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_politics_influence",
            number = "No. 17",
            title = "The Political Operative: Cultural Power & SCC Laws",
            subtitle = "Campaign Endorsements, Lobbying, SCC Regulations & State Power",
            price = 90000.0,
            coverGradient = listOf(Color(0xFF261D10), Color(0xFF130E07), Color(0xFF080502)),
            accentColor = Color(0xFFD97706),
            chapters = listOf(
                EbookChapter(
                    "Cultural Endorsements in High-Stakes Elections",
                    "When political candidates run for state governor, their campaigns crave celebrity endorsement. Backing the winning candidate can bring tax protections and regulatory favors, but backing the loser risks administrative scrutiny."
                ),
                EbookChapter(
                    "Navigating SCC Regulatory Landmines",
                    "The State Commerce and Securities Commission (SCC) strictly enforces licensing laws. Never pocket designated corporate investment capital, and always file commercial licenses on time."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_merch_supply",
            number = "No. 18",
            title = "Merchandise Maven: Physical Products & Fan Loyalty",
            subtitle = "Gross Margins, Supply Chains, Drop Shipping & Fan Loyalty",
            price = 22000.0,
            coverGradient = listOf(Color(0xFF182E1E), Color(0xFF0A170E), Color(0xFF040B06)),
            accentColor = Color(0xFF10B981),
            chapters = listOf(
                EbookChapter(
                    "The Power of the 80% Gross Margin",
                    "While digital streaming earns fractions of a cent per play, high-quality hoodies, vinyl records, and tour posters can command 75% to 80% gross margins. Merch sales often represent 40% of total tour profits."
                ),
                EbookChapter(
                    "Warehousing & Inventory Forecasting",
                    "Avoid over-ordering slow inventory. Stock limited-edition seasonal drops to generate urgency and prevent unsold capital from sitting trapped in warehouse boxes."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_music_videos",
            number = "No. 19",
            title = "The Music Video Director's Visual Playbook",
            subtitle = "Visual Budgeting, Cinematography, Director Collaborations & Viral Hits",
            price = 24000.0,
            coverGradient = listOf(Color(0xFF2F103B), Color(0xFF17071E), Color(0xFF0B030E)),
            accentColor = Color(0xFFD946EF),
            chapters = listOf(
                EbookChapter(
                    "Storytelling Beyond the Audio",
                    "A music video is not just an accompaniment; it is an enduring cultural artifact. Cinematic storytelling and distinctive color grading can transform a regional club song into an international anthem."
                ),
                EbookChapter(
                    "Budgeting for Maximum ROI",
                    "Spending $20,000 on high-end lighting and expert editing delivers ten times the return of spending $100,000 on rented supercars and generic club sets."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_real_estate",
            number = "No. 20",
            title = "Real Estate & Architecture: Luxury Estates & Sovereign Assets",
            subtitle = "Property Appreciation, Mortgage Leverage, Private Islands & Security",
            price = 70000.0,
            coverGradient = listOf(Color(0xFF381515), Color(0xFF1D0909), Color(0xFF0E0404)),
            accentColor = Color(0xFFEF4444),
            chapters = listOf(
                EbookChapter(
                    "Anchoring Wealth in Real Assets",
                    "Entertainment fortunes can vanish in bankruptcies and speculative crashes. Buying physical land, luxury estates, and commercial real estate locks in permanent equity that appreciates with inflation."
                ),
                EbookChapter(
                    "Home Studios & Creative Sanctuaries",
                    "Building a state-of-the-art recording studio inside your primary residence eliminates commercial studio booking fees and provides 24/7 creative autonomy."
                )
            )
        )
    )

    val BOOKS_PART3: List<EbookDefinition> = listOf(
        EbookDefinition(
            id = "ebook_studio_engineering",
            number = "No. 21",
            title = "The Audio Engineer's Bible: Mixing & Acoustic Mastery",
            subtitle = "Mixing, Mastering, Analog Warmth, DAW Plugins & Studio Acoustics",
            price = 16000.0,
            coverGradient = listOf(Color(0xFF18303B), Color(0xFF0A171D), Color(0xFF040A0D)),
            accentColor = Color(0xFF06B6D4),
            chapters = listOf(
                EbookChapter(
                    "Frequency Balance & Headroom",
                    "The difference between an amateur demo and a radio-ready master lies in surgical EQ cleanup and dynamic multi-band compression. Clean sub-bass frequencies and crisp high-end air ensure your tracks hit hard on club sound systems."
                ),
                EbookChapter(
                    "Vocal Chain Architecture",
                    "Pairing high-end tube microphones with boutique analog preamps captures intimate vocal harmonics that capture audiences' hearts."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_underworld_survival",
            number = "No. 22",
            title = "Street Smarts & Shadow Pitfalls: Surviving Industry Traps",
            subtitle = "Extortion Defense, Shady Syndicates & Legal Pitfalls",
            price = 55000.0,
            coverGradient = listOf(Color(0xFF261A26), Color(0xFF130D13), Color(0xFF080508)),
            accentColor = Color(0xFF94A3B8),
            chapters = listOf(
                EbookChapter(
                    "Spotting Predatory Deals",
                    "When smooth-talking promoters offer unchecked cash upfront with vague contract clauses, run. Shady syndicates use predatory loans and extortion to control talented artists who fail to read the fine print."
                ),
                EbookChapter(
                    "Retaining Uncompromised Independence",
                    "Always employ independent legal counsel. The most expensive contract you will ever sign is the one you agreed to without a top entertainment lawyer."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_philanthropy_legacy",
            number = "No. 23",
            title = "The Philanthropist's Legacy: Foundations & Enduring Good",
            subtitle = "Building Schools, Charity Galas, Tax Deductions & Enduring Good",
            price = 32000.0,
            coverGradient = listOf(Color(0xFF103328), Color(0xFF071913), Color(0xFF030C09)),
            accentColor = Color(0xFF10B981),
            chapters = listOf(
                EbookChapter(
                    "Giving Back to Build Enduring Roots",
                    "True influence is measured by what you build for others. Funding music education foundations and urban arts grants cements your status as a beloved community icon."
                ),
                EbookChapter(
                    "Philanthropic Tax Efficiencies",
                    "Legitimate charitable foundations allow successful moguls to direct tax-deductible capital into causes they cherish rather than losing it to administrative bureaucracy."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_vocal_health",
            number = "No. 24",
            title = "Vocal Anatomy & Stamina: Protecting Your Instrument",
            subtitle = "Pitch Accuracy, Breath Control, Range Expansion & Vocal Nodes Prevention",
            price = 14000.0,
            coverGradient = listOf(Color(0xFF381530), Color(0xFF1D0A18), Color(0xFF0E040C)),
            accentColor = Color(0xFFEC4899),
            chapters = listOf(
                EbookChapter(
                    "Your Voice is a Living Muscle",
                    "Singing five nights a week on tour without proper diaphragmatic breath support leads to vocal cord hemorrhages and debilitating nodes. Warm-up scales and hydration are non-negotiable."
                ),
                EbookChapter(
                    "Unlocking Vocal Skill 80+",
                    "Disciplined daily training expands your vocal range and resonance, satisfying the high vocal skill requirements demanded by WGT audition judges and cinematic casting boards."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_artist_roster",
            number = "No. 25",
            title = "The Syndicate: Managing Massive Artist Rosters",
            subtitle = "Talent Scouting, Executive Leadership, Contract Buyouts & Label War",
            price = 85000.0,
            coverGradient = listOf(Color(0xFF28183B), Color(0xFF140A1F), Color(0xFF09040E)),
            accentColor = Color(0xFFA855F7),
            chapters = listOf(
                EbookChapter(
                    "Transitioning from Artist to Executive",
                    "The greatest moguls don't just release their own tracks; they build record labels and sign promising talent. Scouting hungry young artists and funding their development creates multi-generational enterprise value."
                ),
                EbookChapter(
                    "Resolving Creative Egos",
                    "Managing artistic talent requires diplomatic tact and firm financial guardrails. Reward loyalty, foster healthy competition, and insulate your label against external poachers."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_personal_wealth",
            number = "No. 26",
            title = "Personal Finance for Moguls: Liquid Reserves & Longevity",
            subtitle = "Lifestyle Creep, Tax Optimisation, Structured Loans & Liquid Reserves",
            price = 48000.0,
            coverGradient = listOf(Color(0xFF183325), Color(0xFF0B1912), Color(0xFF040C08)),
            accentColor = Color(0xFF22C55E),
            chapters = listOf(
                EbookChapter(
                    "The Danger of Lifestyle Inflation",
                    "Artists earning $1M a year often go broke because their private jet charters and luxury expenses cost $1.2M. Live below your means during career booms to build an unshakeable cash fortress."
                ),
                EbookChapter(
                    "Structured Commercial Loans",
                    "When borrowing capital for venture expansions, ensure loan repayments are serviced from enterprise operating profits rather than personal living savings."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_stadium_festivals",
            number = "No. 27",
            title = "Festival Curation: Stadium Production & Crowd Kinetics",
            subtitle = "Stage Pyrotechnics, Crowd Control, Headliner Bidding & Concession Margins",
            price = 60000.0,
            coverGradient = listOf(Color(0xFF382310), Color(0xFF1D1107), Color(0xFF0E0803)),
            accentColor = Color(0xFFF59E0B),
            chapters = listOf(
                EbookChapter(
                    "Designing the Ultimate Fan Experience",
                    "Curating your own 50,000-capacity festival gives you complete control over ticket pricing, VIP hospitality packages, and beverage concessions, multiplying live revenues exponentially."
                ),
                EbookChapter(
                    "Production Safety & Pyrotechnics",
                    "High-voltage stadium stages require licensed audio engineers and top-tier security chiefs to prevent logistical accidents and crowd crushes."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_collaboration_art",
            number = "No. 28",
            title = "The Art of the Feature: Chemistry, Ego & Shared Acclaim",
            subtitle = "Creative Chemistry, Ego Management, Feature Fees & Cross-Fanbase Growth",
            price = 18000.0,
            coverGradient = listOf(Color(0xFF2B1038), Color(0xFF15071C), Color(0xFF0A030D)),
            accentColor = Color(0xFFC084FC),
            chapters = listOf(
                EbookChapter(
                    "Unlocking Synergistic Fanbases",
                    "Collaborating with an artist who has 200K fans in another genre introduces your voice to thousands of fresh ears. High relationship scores with fellow artists unlock discounted feature fees and viral joint singles."
                ),
                EbookChapter(
                    "Navigating Songwriting Credits",
                    "Clarify publishing splits and master royalties in writing before hitting record in the studio. Respectful professional partnerships outlast fleeting studio sessions."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_defi_protocols",
            number = "No. 29",
            title = "DeFi Architect: Smart Contracts, Pools & DAO Mechanics",
            subtitle = "Automated Market Makers, Impermanent Loss, Flash Loans & DAO Voting",
            price = 95000.0,
            coverGradient = listOf(Color(0xFF0F2642), Color(0xFF071423), Color(0xFF020911)),
            accentColor = Color(0xFF0284C7),
            chapters = listOf(
                EbookChapter(
                    "Algorithmic Liquidity Provision",
                    "Automated market makers (AMMs) incentivize community users to stake tokens into liquidity pools. Maintaining deep pool reserves stabilizes token price volatility on Genesis exchanges."
                ),
                EbookChapter(
                    "Decentralized Governance & Community Consensus",
                    "Distributing governance tokens allows loyal fans and protocol users to vote on platform upgrades, transforming passive listeners into active stakeholders."
                )
            )
        ),
        EbookDefinition(
            id = "ebook_twenty_year_mogul",
            number = "No. 30",
            title = "The Sovereign Mogul: Compounding Wisdom Across Decades",
            subtitle = "Compounding Wisdom, Enduring Dynasties, Timeless Art & Sovereign Freedom",
            price = 100000.0,
            coverGradient = listOf(Color(0xFF3B2E0A), Color(0xFF1F1804), Color(0xFF0E0B01)),
            accentColor = Color(0xFFFBBF24),
            chapters = listOf(
                EbookChapter(
                    "Wisdom Must Compound",
                    "In the living world of Artist Story, decisions never follow fixed puzzles. The same choice made in a booming economy will produce completely different outcomes during a recession. Those who study systems, adapt to changing trends, and act with patience build empires that endure for generations."
                ),
                EbookChapter(
                    "The True Definition of Success",
                    "Success is not having ten billion dollars in an account. Success is sovereign creative freedom — owning your master recordings, controlling thriving businesses, holding the deep respect of your peers, and mentoring the next generation of visionary creators."
                )
            )
        )
    )

    val ALL_EBOOKS: List<EbookDefinition> = BOOKS_PART1 + BOOKS_PART2 + BOOKS_PART3
}
