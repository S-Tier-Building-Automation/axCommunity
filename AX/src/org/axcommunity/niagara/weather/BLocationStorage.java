package org.axcommunity.niagara.weather;

import javax.baja.sys.BComponent;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BLocationStorage
extends BComponent
{
  //CAXX0001, Abbotsford
  //CAXX0002, Agassiz
  //CAXX0003, Ahousat
  //CAXX0004, Airdrie
  //CAXX0005, Ajax
  //CAXX0007, Albertville
  //CAXX0008, Amherst
  //CAXX0009, Amherstburg
  //CAXX0010, Anzac
  //CAXX0011, Arborg
  //CAXX0012, Ascot Corner
  //CAXX0013, Ashville
  //CAXX0014, Atikokan
  //CAXX0015, Aylesford
  //CAXX0016, Aylmer
  //CAXX0017, Baden
  //CAXX0018, Bagotville
  //CAXX0019, Baie-Comeau
  //CAXX0020, Baie-Saint-Paul
  //CAXX0021, Baker Lake
  //CAXX0022, Bala
  //CAXX0023, Banff
  //CAXX0024, Barachois
  //CAXX0025, Barrie
  //CAXX0026, Bayfield
  //CAXX0027, Beamsville
  //CAXX0028, Beauport
  //CAXX0029, Beausejour
  //CAXX0030, Beaver Cove
  //CAXX0031, Becancour
  //CAXX0032, Bedford
  //CAXX0033, Belcourt
  //CAXX0034, Belleville
  //CAXX0035, Belmont
  //CAXX0036, Big Brook
  //CAXX0037, Birch Hills
  //CAXX0038, Birken
  //CAXX0039, Bloedel
  //CAXX0040, Boucherville
  //CAXX0041, Bowmanville
  //CAXX0042, Bracebridge
  //CAXX0043, Brampton
  //CAXX0044, Brandon
  //CAXX0045, Brantford
  //CAXX0046, Brighton
  //CAXX0047, Bromptonville
  //CAXX0048, Bruce Mines
  //CAXX0049, Burin
  //CAXX0050, Burlington
  //CAXX0051, Burnaby
  //CAXX0052, Bylot
  //CAXX0053, Cacouna
  //CAXX0054, Calgary
  ////CAXX0055, Callander
  //CAXX0056, Cambridge
  //CAXX0057, Campbell River
  //CAXX0058, Campbellton
  //CAXX0059, Canmore
  //CAXX0060, Cap Chat
  //CAXX0061, Cap-de-la-Madeleine
  //CAXX0062, Cap-des-Rosiers
  //CAXX0063, Carcross
  //CAXX0064, Caronport
  //CAXX0065, Carp
  //CAXX0066, Castlegar
  //CAXX0067, Castor
  //CAXX0068, Chambord
  //CAXX0069, Champagne
  //CAXX0070, Chapais
  //CAXX0071, Charlesbourg
  //CAXX0072, Charlie Lake
  //CAXX0073, Charlottetown
  //CAXX0074, Chatham
  //CAXX0075, Chibougamau
  //CAXX0076, Chicoutimi
  //CAXX0077, Chilliwack
  //CAXX0078, Churchill
  //CAXX0079, Chute-aux-Outardes
  //CAXX0080, Clairmont
  //CAXX0081, Clarke City
  //CAXX0082, Clinton
  //CAXX0083, Coal Harbour
  //CAXX0084, Coaldale
  //CAXX0085, Cobden
  //CAXX0086, Cochrane
  //CAXX0087, Codroy
  //CAXX0088, Colborne
  //CAXX0089, Cold Lake
  //CAXX0090, Collingwood
  //CAXX0091, Collins Bay
  //CAXX0092, Comox
  //CAXX0093, Coniston
  //CAXX0094, Copper Cliff
  //CAXX0095, Copper Creek
  //CAXX0096, Cormorant
  //CAXX0097, Corner Brook
  //CAXX0098, Coronation
  //CAXX0099, Courtenay
  //CAXX0100, Courtright
  //CAXX0101, Cranberry Portage
  //CAXX0102, Cranbrook
  //CAXX0103, Cumberland
  //CAXX0104, Cut Knife
  //CAXX0105, Dafoe
  //CAXX0106, Dalhousie
  //CAXX0107, Dartmouth
  //CAXX0108, Dauphin
  //CAXX0109, Dawson
  //CAXX0110, Dawson Creek
  //CAXX0111, Debert
  //CAXX0113, Delhi
  //CAXX0114, Denfield
  //CAXX0115, Denholm
  //CAXX0116, Deroche
  //CAXX0117, Devon
  //CAXX0118, Dieppe
  //CAXX0119, Digges
  //CAXX0120, Dominion
  //CAXX0121, Douglastown
  //CAXX0122, Dundas
  //CAXX0123, Dunmore
  //CAXX0124, East Point
  //CAXX0125, Echo Bay
  //CAXX0126, Edmonton
  //CAXX0127, Edmundston
  //CAXX0128, Edson
  //CAXX0129, Elliot Lake
  //CAXX0130, Elmsdale
  //CAXX0131, Enderby
  //CAXX0132, Esquimalt
  //CAXX0133, Essex
  //CAXX0134, Estevan
  //CAXX0135, Ethelbert
  //CAXX0136, Etobicoke
  //CAXX0137, Fairmont Hot Springs
  //CAXX0138, Falconbridge
  //CAXX0139, Fiske
  //CAXX0140, Flaxcombe
  //CAXX0141, Flin Flon
  //CAXX0142, Floral
  //CAXX0143, Fort Erie
  //CAXX0144, Fort Frances
  //CAXX0145, Fort MacKay
  //CAXX0146, Fort McMurray
  //CAXX0147, Fort Nelson
  //CAXX0148, Fort Saint John
  //CAXX0149, Fort Saskatchewan
  //CAXX0150, Fort Steele
  //CAXX0151, Fredericton
  //CAXX0152, Freshford
  //CAXX0153, Gambo
  //CAXX0154, Gananoque
  //CAXX0155, Gander
  //CAXX0156, Garibaldi
  //CAXX0157, Gaspe
  //CAXX0158, Gatineau
  //CAXX0159, Geraldton
  //CAXX0160, Gillam
  //CAXX0161, Gimli
  //CAXX0162, Glace Bay
  //CAXX0163, Glenwood
  //CAXX0164, Godbout
  //CAXX0165, Goderich
  //CAXX0166, Godreau
  //CAXX0167, Golden
  //CAXX0168, Goose Bay
  //CAXX0169, Gracefield
  //CAXX0170, Grand Bay
  //CAXX0171, Grand Centre
  //CAXX0172, Grand Coulee
  //CAXX0173, Grand Falls
  //CAXX0174, Grand View
  //CAXX0175, Grande Prairie
  //CAXX0176, Gravenhurst
  //CAXX0177, Greenwood
  //CAXX0178, Grimsby
  //CAXX0179, Grindrod
  //CAXX0180, Grosse Isle
  //CAXX0181, Guelph
  //CAXX0182, Hague
  //CAXX0183, Halifax
  //CAXX0184, Hamilton
  //CAXX0185, Hampden
  //CAXX0186, Hampton
  //CAXX0187, Hanna
  //CAXX0188, Hare Bay
  //CAXX0189, Hawk Junction
  //CAXX0190, Hawk Lake
  //CAXX0191, Hebron
  //CAXX0192, Hope
  //CAXX0193, Howley
  //CAXX0194, Hull
  //CAXX0195, Ignace
  //CAXX0196, Iles-de-la-Madeleine
  //CAXX0197, Ilford
  //CAXX0198, Ingersoll
  //CAXX0199, Innisfail
  //CAXX0201, Invermere
  //CAXX0202, Iqaluit
  //CAXX0203, Irvine
  //CAXX0204, Isle aux Morts
  //CAXX0205, Jasper
  //CAXX0206, Jellicoe
  //CAXX0207, Joliette
  //CAXX0208, Jonquiere
  //CAXX0209, Kakabeka Falls
  //CAXX0210, Kakawis
  //CAXX0211, Kamloops
  //CAXX0212, Kanata
  //CAXX0213, Kapuskasing
  //CAXX0214, Keene
  //CAXX0215, Keewatin
  //CAXX0216, Kelowna
  //CAXX0217, Kemnay
  //CAXX0218, Kenora
  //CAXX0219, Kentville
  //CAXX0220, Keswick
  //CAXX0221, Kildonan
  //CAXX0222, Kimberley
  //CAXX0223, Kincardine
  //CAXX0224, Kindersley
  //CAXX0225, Kingston
  //CAXX0226, Kinuso
  //CAXX0227, Kitchener
  //CAXX0228, Kitscoty
  //CAXX0229, La Baie
  //CAXX0230, La Corey
  //CAXX0231, La Grande
  //CAXX0232, La Malbaie
  //CAXX0233, La Ronge
  //CAXX0234, La Salle
  //CAXX0235, Labrador City
  //CAXX0236, Lachine
  //CAXX0237, Lacombe
  //CAXX0238, Lake Louise
  //CAXX0239, Lakefield
  //CAXX0240, Lamaline
  //CAXX0241, Langham
  //CAXX0242, Lashburn
  //CAXX0243, Laval
  //CAXX0244, Lawn
  //CAXX0245, Lennoxville
  //CAXX0246, Lethbridge
  //CAXX0247, Levis
  //CAXX0248, Lions Head
  //CAXX0249, L'Isle-Verte
  //CAXX0250, Lively
  //CAXX0251, Lloydminster
  //CAXX0252, Lochalsh
  //CAXX0253, Loggieville
  //CAXX0254, Lomond
  //CAXX0255, London
  //CAXX0256, Longlac
  //CAXX0257, Longueuil
  //CAXX0258, Loretteville
  //CAXX0259, Lucan
  //CAXX0260, Luceville
  //CAXX0261, Lumsden
  //CAXX0262, Lund
  //CAXX0263, MacAlister
  //CAXX0264, MacCan
  //CAXX0265, MacDowall
  //CAXX0266, MacKenzie
  //CAXX0267, Magog
  //CAXX0268, Magrath
  //CAXX0269, Maitland
  //CAXX0270, Malartic
  //CAXX0271, Malton
  //CAXX0272, Maniwaki
  //CAXX0273, Markham
  //CAXX0274, Marlboro
  //CAXX0275, Marysville
  //CAXX0276, Massey
  //CAXX0277, Matane
  //CAXX0278, Matapedia
  //CAXX0279, Mattice
  //CAXX0280, McGregor
  //CAXX0281, Meaford
  //CAXX0282, Medicine Hat
  //CAXX0283, Memramcook
  //CAXX0284, Merville
  //CAXX0285, Messines
  //CAXX0286, Midale
  //CAXX0287, Middleton
  //CAXX0288, Midhurst
  //CAXX0289, Milton
  //CAXX0290, Minaki
  //CAXX0291, Mine Centre
  //CAXX0292, Minnedosa
  //CAXX0293, Miramichi
  //CAXX0294, Mission
  //CAXX0295, Mississauga
  //CAXX0296, Moisie
  //CAXX0297, Moncton
  //CAXX0298, Monte Creek
  //CAXX0299, Mont-Joli
  //CAXX0300, Mont-Laurier
  //CAXX0301, Montreal
  //CAXX0302, Moonbeam
  //CAXX0303, Mooretown
  //CAXX0304, Moose Factory
  //CAXX0305, Moose Jaw
  //CAXX0306, Moose Lake
  //CAXX0307, Moose River
  //CAXX0308, Moosonee
  //CAXX0309, Morinville
  //CAXX0310, Mount Pearl
  //CAXX0311, Mount Stewart
  //CAXX0312, Mud River
  //CAXX0313, Nanaimo
  //CAXX0314, Nanticoke
  //CAXX0315, Nelson
  //CAXX0316, Nelson House
  //CAXX0317, New Waterford
  //CAXX0318, New Westminster
  //CAXX0319, Newcastle
  //CAXX0320, Nezah
  //CAXX0321, Niagara Falls
  //CAXX0322, Nicolet
  //CAXX0323, Nipigon
  //CAXX0324, Nobel
  //CAXX0325, North Battleford
  //CAXX0326, North Bay
  //CAXX0327, North Sydney
  //CAXX0328, North Vancouver
  //CAXX0329, North West River
  //CAXX0330, North York
  //CAXX0331, Notre-Dame-du-Portage
  //CAXX0332, Oak Bay
  //CAXX0333, Oakville
  //CAXX0334, Okanagan Landing
  //CAXX0335, Okotoks
  //CAXX0336, Oliver
  //CAXX0337, Omemee
  //CAXX0338, One Hundred Fifty Mile House
  //CAXX0339, Opasatika
  //CAXX0340, Orillia
  //CAXX0341, Oromocto
  //CAXX0342, Oshawa
  //CAXX0343, Ottawa
  //CAXX0344, Outremont
  //CAXX0345, Owen Sound
  //CAXX0346, Oyama
  //CAXX0347, Parksville
  //CAXX0348, Parry Sound
  //CAXX0349, Parson
  //CAXX0350, Pasqua
  //CAXX0351, Peachland
  //CAXX0352, Peers
  //CAXX0353, Pemberton
  //CAXX0354, Pembroke
  //CAXX0355, Penticton
  //CAXX0356, Petawawa
  //CAXX0357, Peterborough
  //CAXX0358, Pickering
  //CAXX0359, Pilot Butte
  //CAXX0360, Point Edward
  //CAXX0361, Pointe-Lebel
  //CAXX0362, Port Alberni
  //CAXX0363, Port Colborne
  //CAXX0364, Port Coquitlam
  //CAXX0365, Port Dover
  //CAXX0366, Port Edward
  //CAXX0367, Port Essington
  //CAXX0368, Port Hardy
  //CAXX0369, Port McNeill
  //CAXX0370, Port Moody
  //CAXX0371, Port Simpson
  //CAXX0372, Port-aux-Basques
  //CAXX0373, Port-Cartier
  //CAXX0374, Pouch Cove
  //CAXX0375, Powassan
  //CAXX0376, Powell River
  //CAXX0377, Preston
  //CAXX0378, Price
  //CAXX0379, Prince
  //CAXX0380, Prince Albert
  //CAXX0381, Prince George
  //CAXX0382, Prince Rupert
  //CAXX0383, Prospector
  //CAXX0384, Qualicum Beach
  //CAXX0385, Quebec
  //CAXX0386, Queen Charlotte
  //CAXX0387, Quinsam
  //CAXX0388, Radisson
  //CAXX0389, Radium Hot Springs
  //CAXX0390, Rae
  //CAXX0391, Raleigh
  //CAXX0392, Raymond
  //CAXX0393, Read Island
  //CAXX0394, Red Deer
  //CAXX0395, Red Rock
  //CAXX0396, Redcliff
  //CAXX0397, Regina
  //CAXX0398, Revelstoke
  //CAXX0399, Rhein
  //CAXX0400, Richmond
  //CAXX0401, Richmond Hill
  //CAXX0402, Rimouski
  //CAXX0403, Riverton
  //CAXX0404, Riviere-a-Claude
  //CAXX0405, Riviere-du-Loup
  //CAXX0406, Roberval
  //CAXX0407, Rolla
  //CAXX0408, Rose Blanche
  //CAXX0409, Rosetown
  //CAXX0410, Rossland
  //CAXX0411, Rothesay
  //CAXX0412, Saanich
  //CAXX0413, Sackville
  //CAXX0414, Saint Albert
  //CAXX0415, Saint Anthony
  //CAXX0416, Saint Catharines
  //CAXX0417, Saint Catharines Arpt
  //CAXX0418, Saint Eleanors
  //CAXX0419, Saint John
  //CAXX0420, St. John's
  //CAXX0421, Saint Lawrence
  //CAXX0422, Saint Leonard
  //CAXX0423, Saint Louis
  //CAXX0424, Saint Marys
  //CAXX0425, Sainte-Catherine
  //CAXX0426, Sainte-Felicite
  //CAXX0427, Sainte-Foy
  //CAXX0428, Saint-Eustache
  //CAXX0429, Saint-Felicien
  //CAXX0430, Saint-Hubert
  //CAXX0431, Saint-Irenee
  //CAXX0432, Saint-Jacques
  //CAXX0433, Saint-Jovite
  //CAXX0434, Saint-Prime
  //CAXX0435, Saint-Simeon
  //CAXX0437, Salmon Arm
  //CAXX0438, Salmon Valley
  //CAXX0439, Saltcoats
  //CAXX0440, Sandspit
  //CAXX0441, Sarnia
  //CAXX0442, Saskatoon
  //CAXX0443, Sault Ste Marie
  //CAXX0444, Savona
  //CAXX0445, Scarborough
  //CAXX0446, Schefferville
  //CAXX0447, Schumacher
  //CAXX0448, Selkirk
  //CAXX0449, Sept-Iles
  //CAXX0450, Sevenpersons
  //CAXX0451, Sexsmith
  //CAXX0452, Shawanga
  //CAXX0453, Shediac
  //CAXX0454, Sherbrooke
  //CAXX0455, Sidney
  //CAXX0456, Sillery
  //CAXX0457, Simcoe
  //CAXX0458, Sipiwesk
  //CAXX0459, Skidegate
  //CAXX0460, Slave Lake
  //CAXX0461, Smithers
  //CAXX0462, Snake River
  //CAXX0463, Soda Creek
  //CAXX0464, Sorel
  //CAXX0465, Sorrento
  //CAXX0466, Souris
  //CAXX0467, South Porcupine
  //CAXX0468, Spanish
  //CAXX0469, Spragge
  //CAXX0470, Springhill
  //CAXX0471, Spruce Grove
  //CAXX0472, Spurfield
  //CAXX0473, Stanley Mission
  //CAXX0474, Stayner
  //CAXX0475, Stephenville
  //CAXX0476, Stillwater
  //CAXX0477, Stonewall
  //CAXX0478, Stoney Creek
  //CAXX0479, Stony Plain
  //CAXX0480, Strathroy
  //CAXX0481, Sturgeon Falls
  //CAXX0482, Sudbury
  //CAXX0483, Summerland
  //CAXX0484, Summerside
  //CAXX0485, Surrey
  //CAXX0486, Swift Current
  //CAXX0487, Sydney
  //CAXX0488, Sydney Mines
  //CAXX0489, Sylvan Lake
  //CAXX0490, Taber
  //CAXX0491, Taylor
  //CAXX0492, Tecumseh
  //CAXX0493, Terrace
  //CAXX0494, The Pas
  //CAXX0495, Thompson
  //CAXX0496, Thornbury
  //CAXX0497, Thorold
  //CAXX0498, Thunder Bay
  //CAXX0499, Tidehead
  //CAXX0500, Tillsonburg
  //CAXX0501, Timmins
  //CAXX0502, Tofino
  //CAXX0503, Torbay
  //CAXX0504, Toronto
  //CAXX0505, Torquay
  //CAXX0506, Tracy
  //CAXX0507, Trail
  //CAXX0508, Trenton
  //CAXX0509, Trois-Rivieres
  //CAXX0510, Truro
  //CAXX0511, Tusket
  //CAXX0512, Tuxford
  //CAXX0513, Tyrone
  //CAXX0514, Ucluelet
  //CAXX0515, Usk
  //CAXX0516, Val-d'Or
  //CAXX0517, Vananda
  //CAXX0518, Vancouver
  //CAXX0519, Vassan
  //CAXX0520, Verdun
  //CAXX0521, Vermilion
  //CAXX0522, Veteran
  //CAXX0523, Victoria
  //CAXX0524, Virden
  //CAXX0525, Waasis
  //CAXX0526, Wabush
  //CAXX0527, Walcott
  //CAXX0528, Waldeck
  //CAXX0529, Waltham Station
  //CAXX0530, Warman
  //CAXX0531, Waterloo
  //CAXX0532, Wawa
  //CAXX0533, Webb
  //CAXX0534, Wedgeport
  //CAXX0535, Welland
  //CAXX0536, Wembley
  //CAXX0537, Weyburn
  //CAXX0538, Whistler
  //CAXX0539, Whitby
  //CAXX0540, Whitehorse
  //CAXX0541, Wiarton
  //CAXX0542, Williams Lake
  //CAXX0543, Willow River
  //CAXX0544, Willowbrook
  //CAXX0545, Windermere
  //CAXX0546, Windsor
  //CAXX0547, Winnipeg
  //CAXX0548, Wishart
  //CAXX0549, Witless Bay
  //CAXX0550, Wolfe Island
  //CAXX0551, Woodbridge
  //CAXX0552, Woodstock
  //CAXX0553, Wymark
  //CAXX0554, Wynyard
  //CAXX0555, Yale
  //CAXX0556, Yarmouth
  //CAXX0557, Yellowknife
  //CAXX0558, Yorkton
  //CAXX0559, Zehner
  //CAXX0560, Fort Reliance
  //CAXX0561, Buffalo Narrows
  //CAXX0562, Lynn Lake
  //CAXX0563, Rankin Inlet
  //CAXX0564, Edmonton Stony Plain Alta.
  //CAXX0565, Edmonton Namao Alta.
  //CAXX0567, Edmonton International
  //CAXX0568, Meadow Lake
  //CAXX0569, Eastend Cypress
  //CAXX0570, Rockglen
  //CAXX0571, Val Marie Southeast
  //CAXX0572, Norway House
  //CAXX0573, Island Lake
  //CAXX0574, Churchill Falls
  //CAXX0575, Burgeo
  //CAXX0576, Bonavista
  //CAXX0577, Lac St. Pierre
  //CAXX0579, Squamish Airport
  //CAXX0580, Badger
  //CAXX0581, Western Head
  //CAXX0582, Port Weller
  //CAXX0583, Upsala
  //CAXX0584, Swan River
  //CAXX0585, Wasagaming
  //CAXX0586, Melita
  //CAXX0587, Kindakun Rocks
  //CAXX0590, Rosetown East
  //CAXX0591, Sable Island
  //CAXX0592, Shearwater
  //CAXX0593, St. Stephen
  //CAXX0594, Miscou Island
  //CAXX0595, Gore Bay
  //CAXX0596, Argentia
  //CAXX0597, Blanc Sablon
  //CAXX0598, Natashquan
  //CAXX0600, Mary's Harbour
  //CAXX0601, Cartwright
  //CAXX0602, Matagami
  //CAXX0603, Sioux Lookout
  //CAXX0604, Big Trout Lake
  //CAXX0605, Lansdowne House
  //CAXX0606, Red Lake
  //CAXX0607, Broadview
  //CAXX0609, Hudson Bay
  //CAXX0610, Lloydminister
  //CAXX0611, Lytton
  //CAXX0612, Estevan Point
  //CAXX0613, Langara
  //CAXX0614, Kuujjuarapik
  //CAXX0615, Kuujjuaq
  //CAXX0616, Inukjuak
  //CAXX0617, Cape Dorset
  //CAXX0618, Coral Harbour
  //CAXX0619, Lac La Biche
  //CAXX0620, Fort Chipewyan
  //CAXX0621, Fort Smith
  //CAXX0622, Hay River
  //CAXX0624, Fort Simpson
  //CAXX0625, Uranium City
  //CAXX0626, Waskaganish
  //CAXX0627, Watson Lake
  //CAXX0628, Earlton
  //CAXX0629, Puntzi Mountain
  //CAXX0630, High Level
  //CAXX0631, Peace River
  //CAXX0632, Inuvik
  //CAXX0640, Algonquin Park
  //CAXX0641, Alliston
  //CAXX0642, Bathurst
  //CAXX0643, Chapleau
  //CAXX0644, Charlevoix
  //CAXX0645, Ear Falls
  //CAXX0646, Haliburton
  //CAXX0647, Hearst
  //CAXX0648, Huntsville
  //CAXX0649, La Tuque
  //CAXX0650, Mirabel
  //CAXX0651, Muskoka
  //CAXX0652, Rondeau
  //CAXX0653, Gretna
  //CAXX0654, Port Hawkesbury
  //CAXX0655, New Glasgow
  //CAXX0656, Cobourg
  //CAXX0657, Haileybury
  //CAXX0658, Hawkesbury
  //CAXX0659, Kirkland Lake
  //CAXX0660, Lindsay
  //CAXX0661, Marathon
  //CAXX0662, Orangeville
  //CAXX0663, Midland
  //CAXX0664, Newmarket
  //CAXX0665, Smiths Falls
  //CAXX0666, Bancroft
  //CAXX0667, Leamington
  //CAXX0668, Picton
  //CAXX0669, Port Elgin
  //CAXX0670, Mount Forest
  //CAXX0671, Cornwall
  //CAXX0672, Drummondville
  //CAXX0673, Saint-Hyacinthe
  //CAXX0674, Kejimkujik National Park
  //CAXX0675, Armstrong
  //CAXX0676, Portage la Prairie
  //CAXX0677, Brockville
  //CAXX0678, Dryden
  //CAXX0679, St. Thomas
  //CAXX0680, Old Crow
  //CAXX0681, Minto Bridge
  //CAXX0682, Mayo
  //CAXX0683, Burwash Landing
  //CAXX0684, Norman Wells
  //CAXX0685, Beaver Creek
  //CAXX0686, Tuktoyaktuk
  //CAXX0687, Fort McPherson
  //CAXX0688, Teslin
  //CAXX0689, Faro
  //CAXX0690, Snowdrift
  //CAXX0691, Fort Franklin
  //CAXX0692, Arivat
  //CAXX0693, Whale Cove
  //CAXX0694, Dease Lake
  //CAXX0695, Chetwynd
  //CAXX0696, Mackenzie
  //CAXX0697, Southend
  //CAXX0698, Pangnirtung
  //CAXX0699, Lake Harbour
  //CAXX0700, Hopedale
  //CAXX0701, Fairview
  //CAXX0702, Whitecourt
  //CAXX0703, Moose Heights
  //CAXX0704, Mundare
  //CAXX0705, Vegreville
  //CAXX0706, Melfort
  //CAXX0707, Blue River
  //CAXX0708, Watrous
  //CAXX0709, Ralston
  //CAXX0710, Suffield
  //CAXX0711, Schuler
  //CAXX0712, Victoria Beach
  //CAXX0713, Harve-St-Pierre
  //CAXX0714, Nakusp
  //CAXX0715, Brooks
  //CAXX0716, Englee
  //CAXX0717, Chevery
  //CAXX0718, Campbell Island
  //CAXX0719, Clinton
  //CAXX0720, Rocky Mountain House
  //CAXX0721, Drumheller
  //CAXX0722, Three Hills
  //CAXX0723, Bergen
  //CAXX0724, Esther
  //CAXX0725, Leader
  //CAXX0726, Fisher Branch
  //CAXX0727, Pickle Lake
  //CAXX0728, Shoal Cove
  //CAXX0729, Bow Island
  //CAXX0730, Maple Creek
  //CAXX0731, Madeleine-Centre
  //CAXX0732, Princeton
  //CAXX0733, Creston
  //CAXX0734, Coleman
  //CAXX0735, Brocket
  //CAXX0736, Pincher Creek
  //CAXX0737, Claresholm
  //CAXX0738, Coronhach
  //CAXX0739, Pilot Mound
  //CAXX0740, Morden
  //CAXX0741, Rainy River
  //CAXX0742, New Carlisle
  //CAXX0743, Rouyn
  //CAXX0744, Chute-des-Passes
  //CAXX0745, Amqui
  //CAXX0746, Port-Menier
  //CAXX0747, Sparwood
  //CAXX0748, Del Bonita
  //CAXX0749, Emerson
  //CAXX0750, Killarney
  //CAXX0751, McAdam
  //CAXX0752, Ingonish
  //CAXX0753, Grand Etang
  //CAXX0754, Antigonish
  //CAXX0755, Cape Race
  //CAXX0756, Shawinigan
  //CAXX0757, Notre-Dame-de-la-Salette
  //CAXX0758, Papineauville
  //CAXX0759, Beaupre
  //CAXX0760, Beauceville
  //CAXX0761, Portneuf
  //CAXX0762, Woodstock
  //CAXX0763, Dipper Harbour West
  //CAXX0764, Cornwall
  //CAXX0765, Walkerton
  //CAXX0766, Lunenburg
  //CAXX0767, Canso
  //CAXX0768, Chatham
  //CAXX0769, Winchester
  //CAXX0770, Haines Junction
  //CAXX0771, Lupin
  //CAXX0772, Spiritwood
  //CAXX0773, Quesnel
  //CAXX0774, Grand Rapids
  //CAXX0775, Longue-Pointe
  //CAXX0776, Twillingate
  //CAXX0777, Cardston
  //CAXX0778, Carman
  //CAXX0779, Parent
  //CAXX0780, Miami
  //CAXX0781, Cheticamp
  //CAXX0782, Degelis
  //CAXX0783, Kemptville
  //CAXX0784, Freeport
  //CAXX0785, Burns Lake
  //CAXX0786, Milk River
  //CAXX0787, Ville-Marie
  //CAXX0788, Angliers
  //CAXX0800, Kananaskis
  //CAXX0801, West Vancouver
  //CAXX0802, Fernie
  //CAXX0803, Mount Washington
  //CAXX0804, Panorama
  //CAXX0805, Silver Star Mountain
  //CAXX0806, Sun Peaks
  //CAXX0807, La Riviere
  //CAXX0808, Calabogie
  //CAXX0809, Coldwater
  //CAXX0810, Saint-Sauveur
  //CAXX0811, Mont-Tremblant
  //CAXX0812, Petite-Riviere-Saint-Francois
  //CAXX0813, Lac Ste. Marie
  //CAXX0814, St-Faustin-Lac-Carre
  //CAXX0815, Orford
  //CAXX0816, Sutton
  //CAXX0817, Mansonville
  //CAXX0818, Bromont
  //CAXX0819, Stoneham
  //CAXX0820, Assiniboia
  //CAXX0821, Liverpool+Bay
  //CAXX0822, Nagagami
  //CAXX0823, North+Point
  //CAXX0824, Beaver+Island
  //CAXX0825, Beaconsfield
  //CAXX0826, Osoyoos
  //CAXX0827, Saguenay
  //CAXX0828, Saint-Georges
  //CAXX0829, Alert
  //CAXX0830, Merritt
  //CAXX1000, Bernard+Harbour
  //CAXX1001, Abbey
  //CAXX1002, Abee
  //CAXX1003, Aberdeen
  //CAXX1004, Acadia+Valley
  //CAXX1005, Acme
  //CAXX1006, Acton
  //CAXX1007, Acton+Vale
  //CAXX1008, Advocate+Harbour
  //CAXX1009, Aguanish
  //CAXX1010, Aklavik
  //CAXX1011, Alameda
  //CAXX1012, Albanel
  //CAXX1013, Alberton
  //CAXX1014, Alert+Bay
  //CAXX1015, Alexander
  //CAXX1016, Alexandria
  //CAXX1017, Alexis+Creek
  //CAXX1018, Alfred
  //CAXX1019, Alida
  //CAXX1020, Alix
  //CAXX1021, Alkali+Lake
  //CAXX1022, Allan
  //CAXX1023, Alliance
  //CAXX1026, Almonte
  //CAXX1027, Alonsa
  //CAXX1028, Alsask
  //CAXX1029, Altona
  //CAXX1030, Alvena
  //CAXX1031, Amos
  //CAXX1032, Andrew
  //CAXX1033, Angus
  //CAXX1034, Anjou
  //CAXX1035, Annapolis+Royal
  //CAXX1036, Apsley
  //CAXX1037, Arborfield
  //CAXX1038, Arcola
  //CAXX1039, Arctic+Bay
  //CAXX1040, Ardrossan
  //CAXX1041, Argyle
  //CAXX1042, Arichat
  //CAXX1043, Armagh
  //CAXX1045, Armstrong+Station
  //CAXX1046, Arnprior
  //CAXX1047, Arntfield
  //CAXX1048, Arthur
  //CAXX1049, Arviat
  //CAXX1050, Asbestos
  //CAXX1051, Ashcroft
  //CAXX1052, Ashern
  //CAXX1053, Ashmont
  //CAXX1054, Asquith
  //CAXX1055, Athabasca
  //CAXX1056, Atlee
  //CAXX1057, Atlin
  //CAXX1059, Aurora
  //CAXX1060, Austin
  //CAXX1061, Authier
  //CAXX1062, Authier-Nord
  //CAXX1063, Avola
  //CAXX1064, Avonlea
  //CAXX1066, Ayr
  //CAXX1067, Ayton
  //CAXX1068, Baddeck
  //CAXX1069, Baie+Verte
  //CAXX1070, Baie-d+Urfe
  //CAXX1071, Baie-Des-Sables
  //CAXX1072, Baie-Sainte-Catherine
  //CAXX1073, Balcarres
  //CAXX1074, Baldur
  //CAXX1075, Balfour
  //CAXX1076, Bamfield
  //CAXX1077, Barons
  //CAXX1078, Barraute
  //CAXX1079, Barrhead
  //CAXX1080, Barriere
  //CAXX1081, Barrington+Passage
  //CAXX1082, Barwick
  //CAXX1083, Bashaw
  //CAXX1084, Bassano
  //CAXX1085, Bath
  //CAXX1086, Batiscan
  //CAXX1087, Batteau
  //CAXX1088, Battle+Harbour
  //CAXX1089, Battleford
  //CAXX1090, Bauline
  //CAXX1091, Bay+Bulls
  //CAXX1092, Bay+De+Verde
  //CAXX1093, Bay+Roberts
  //CAXX1094, Beachburg
  //CAXX1095, Bear+Lake
  //CAXX1096, Bear+River
  //CAXX1097, Beardmore
  //CAXX1098, Bearn
  //CAXX1099, Bearskin+Lake
  //CAXX1100, Beauharnois
  //CAXX1101, Beaumont
  //CAXX1102, Beauval
  //CAXX1103, Beaverlodge
  //CAXX1104, Beaverton
  //CAXX1106, Beechy
  //CAXX1107, Beiseker
  //CAXX1108, Bell+Island
  //CAXX1109, Bella+Bella
  //CAXX1110, Bella+Coola
  //CAXX1111, Belle+River
  //CAXX1112, Belleoram
  //CAXX1113, Bellevue
  //CAXX1115, Beloeil
  //CAXX1116, Bengough
  //CAXX1117, Benito
  //CAXX1118, Bentley
  //CAXX1119, Berens+River
  //CAXX1120, Berthierville
  //CAXX1121, Berwick
  //CAXX1122, Berwyn
  //CAXX1123, Bethany
  //CAXX1124, Bethune
  //CAXX1125, Betsiamites
  //CAXX1126, Beulah
  //CAXX1127, Bienfait
  //CAXX1128, Big+River
  //CAXX1130, Big+Valley
  //CAXX1131, Biggar
  //CAXX1132, Bindloss
  //CAXX1133, Binscarth
  //CAXX1134, Birch+Island
  //CAXX1135, Birchy+Bay
  //CAXX1136, Birsay
  //CAXX1137, Birtle
  //CAXX1138, Biscotasing
  //CAXX1139, Bissett
  //CAXX1140, Black+Creek
  //CAXX1141, Black+Diamond
  //CAXX1142, Black+Lake
  //CAXX1143, Blackfalds
  //CAXX1144, Blackie
  //CAXX1145, Blackville
  //CAXX1146, Blaine+Lake
  //CAXX1147, Blanc-Sablon
  //CAXX1148, Blenheim
  //CAXX1149, Blind+River
  //CAXX1150, Blue+Ridge
  //CAXX1151, Bobcaygeon
  //CAXX1152, Boiestown
  //CAXX1153, Bois-Des-Filion
  //CAXX1154, Boischatel
  //CAXX1155, Boisdale
  //CAXX1156, Boissevain
  //CAXX1157, Bolton
  //CAXX1158, Bon+Accord
  //CAXX1159, Bonaventure
  //CAXX1160, Bonnyville
  //CAXX1161, Borden
  //CAXX1162, Boswell
  //CAXX1163, Botwood
  //CAXX1164, Bowden
  //CAXX1165, Bowser
  //CAXX1166, Boyle
  //CAXX1167, Bradford
  //CAXX1168, Braeside
  //CAXX1169, Branch
  //CAXX1170, Brechin
  //CAXX1171, Bredenbury
  //CAXX1172, Breton
  //CAXX1173, Bridge+Lake
  //CAXX1174, Bridgetown
  //CAXX1175, Bridgewater
  //CAXX1176, Briercrest
  //CAXX1177, Brigus
  //CAXX1178, Britannia+Beach
  //CAXX1179, Britt
  //CAXX1181, Brochet
  //CAXX1182, Brookfield
  //CAXX1183, Brossard
  //CAXX1184, Brownsburg
  //CAXX1185, Brownvale
  //CAXX1186, Bruno
  //CAXX1187, Bryson
  //CAXX1188, Buchanan
  //CAXX1189, Buchans
  //CAXX1190, Buckhorn
  //CAXX1191, Buckingham
  //CAXX1193, Burstall
  //CAXX1194, Cabano
  //CAXX1195, Cabri
  //CAXX1196, Cache+Creek
  //CAXX1199, Calder
  //CAXX1200, Caledon
  //CAXX1201, Caledon+East
  //CAXX1202, Caledonia
  //CAXX1203, Calling+Lake
  //CAXX1204, Calmar
  //CAXX1205, Cambridge+Bay
  //CAXX1206, Campbellford
  //CAXX1208, Camperville
  //CAXX1209, Camrose
  //CAXX1210, Canal+Flats
  //CAXX1211, Candiac
  //CAXX1212, Cannington
  //CAXX1213, Canoe+Narrows
  //CAXX1214, Canora
  //CAXX1215, Canwood
  //CAXX1216, Cap-Aux-Meules
  //CAXX1217, Cap-Chat
  //CAXX1218, Cap-Rouge
  //CAXX1219, Cape+Broyle
  //CAXX1220, Caplan
  //CAXX1221, Capreol
  //CAXX1222, Caramat
  //CAXX1223, Caraquet
  //CAXX1224, Carberry
  //CAXX1225, Carbonear
  //CAXX1226, Cardigan
  //CAXX1228, Cargill
  //CAXX1229, Carleton+Place
  //CAXX1230, Carlyle
  //CAXX1231, Carmacks
  //CAXX1232, Carmanville
  //CAXX1233, Carnduff
  //CAXX1234, Caroline
  //CAXX1235, Carrot+River
  //CAXX1236, Carseland
  //CAXX1237, Carstairs
  //CAXX1238, Cartier
  //CAXX1240, Casselman
  //CAXX1241, Cat+Lake
  //CAXX1242, Catalina
  //CAXX1243, Causapscal
  //CAXX1244, Cayley
  //CAXX1245, Centralia
  //CAXX1246, Centreville
  //CAXX1247, Cereal
  //CAXX1248, Ceylon
  //CAXX1249, Chalk+River
  //CAXX1250, Chambly
  //CAXX1251, Champion
  //CAXX1252, Champlain
  //CAXX1253, Chandler
  //CAXX1254, Change+Islands
  //CAXX1255, Channel-Port+Aux+Basques
  //CAXX1256, Chapel+Arm
  //CAXX1257, Chaplin
  //CAXX1258, Charlemagne
  //CAXX1260, Charlton
  //CAXX1261, Charny
  //CAXX1262, Chase
  //CAXX1263, Chateauguay
  //CAXX1264, Chatsworth
  //CAXX1265, Chauvin
  //CAXX1266, Chelmsford
  //CAXX1267, Chemainus
  //CAXX1268, Chesley
  //CAXX1269, Chester
  //CAXX1270, Chesterfield
  //CAXX1271, Chesterfield+Inlet
  //CAXX1274, Cheverie
  //CAXX1277, Chomedey
  //CAXX1278, Clarenville
  //CAXX1279, Clearwater
  //CAXX1280, Clericy
  //CAXX1281, Clermont
  //CAXX1282, Climax
  //CAXX1283, Clive
  //CAXX1284, Cloridorme
  //CAXX1285, Clova
  //CAXX1286, Clyde+River
  //CAXX1287, Coaticook
  //CAXX1288, Cobalt
  //CAXX1289, Cobble+Hill
  //CAXX1290, Coboconk
  //CAXX1292, Coderre
  //CAXX1293, Coe+Hill
  //CAXX1294, Colchester
  //CAXX1295, Coleville
  //CAXX1296, Colliers
  //CAXX1297, Colonsay
  //CAXX1298, Colville+Lake
  //CAXX1299, Comber
  //CAXX1300, Come+By+Chance
  //CAXX1301, Commanda
  //CAXX1302, Conception+Harbour
  //CAXX1303, Conche
  //CAXX1304, Conklin
  //CAXX1305, Connaught
  //CAXX1306, Conquest
  //CAXX1307, Consort
  //CAXX1308, Contrecoeur
  //CAXX1309, Cookshire
  //CAXX1310, Coppermine
  //CAXX1311, Coquitlam
  //CAXX1312, Cote-Saint-Luc
  //CAXX1313, Cottam
  //CAXX1314, Coutts
  //CAXX1315, Cow+Head
  //CAXX1316, Cowansville
  //CAXX1317, Cowichan+Bay
  //CAXX1318, Cowley
  //CAXX1319, Crabtree
  //CAXX1320, Craigmyle
  //CAXX1321, Crediton
  //CAXX1322, Creemore
  //CAXX1323, Cremona
  //CAXX1324, Cross+Lake
  //CAXX1325, Crossfield
  //CAXX1326, Crystal+Beach
  //CAXX1327, Crystal+City
  //CAXX1328, Cudworth
  //CAXX1329, Cumberland+House
  //CAXX1330, Cupar
  //CAXX1331, Cypress+River
  //CAXX1332, Czar
  //CAXX1333, Dalmeny
  //CAXX1334, Danville
  //CAXX1335, Dapp
  //CAXX1336, Davidson
  //CAXX1337, Daysland
  //CAXX1338, Debden
  //CAXX1339, Debec
  //CAXX1341, Delburne
  //CAXX1342, Deline
  //CAXX1345, Deloraine
  //CAXX1346, Delson
  //CAXX1347, Denbigh
  //CAXX1348, Denzil
  //CAXX1349, Desbarats
  //CAXX1350, Desbiens
  //CAXX1351, Deschaillons-Sur-Saint-Laurent
  //CAXX1352, Deseronto
  //CAXX1353, Destruction+Bay
  //CAXX1354, Deux-Montagnes
  //CAXX1355, Didsbury
  //CAXX1356, Digby
  //CAXX1357, Dillon
  //CAXX1358, Dingwall
  //CAXX1359, Dinsmore
  //CAXX1360, Disraeli
  //CAXX1361, Dixonville
  //CAXX1362, Doaktown
  //CAXX1363, Dodsland
  //CAXX1364, Dollard-Des-Ormeaux
  //CAXX1365, Dominion+City
  //CAXX1366, Domremy
  //CAXX1367, Donalda
  //CAXX1368, Donnacona
  //CAXX1369, Donnelly
  //CAXX1370, Dorset
  //CAXX1371, Dorval
  //CAXX1372, Douglas+Lake
  //CAXX1373, Dover
  //CAXX1374, Drayton+Valley
  //CAXX1375, Dresden
  //CAXX1376, Drumbo
  //CAXX1377, Dubuisson
  //CAXX1378, Duchess
  //CAXX1379, Duck+Lake
  //CAXX1380, Duncan
  //CAXX1381, Dundalk
  //CAXX1382, Dundurn
  //CAXX1383, Dunnville
  //CAXX1384, Dunsford
  //CAXX1385, Dunster
  //CAXX1386, Duparquet
  //CAXX1387, Dupuy
  //CAXX1388, Durham
  //CAXX1389, Duvernay
  //CAXX1390, Eagle+River
  //CAXX1391, Eaglesham
  //CAXX1392, East+Angus
  //CAXX1393, East+Coulee
  //CAXX1394, East+Pine
  //CAXX1395, Eastend
  //CAXX1396, Eastern+Passage
  //CAXX1397, Eastmain
  //CAXX1398, Eastman
  //CAXX1399, Eastport
  //CAXX1400, Ecum+Secum
  //CAXX1401, Edam
  //CAXX1402, Edgerton
  //CAXX1403, Edzo
  //CAXX1404, Eganville
  //CAXX1405, Elbow
  //CAXX1406, Elgin
  //CAXX1407, Elk+Lake
  //CAXX1408, Elk+Point
  //CAXX1409, Elkhorn
  //CAXX1410, Elko
  //CAXX1411, Elliston
  //CAXX1412, Elm+Creek
  //CAXX1413, Elmira
  //CAXX1414, Elmvale
  //CAXX1415, Elora
  //CAXX1416, Elrose
  //CAXX1418, Empress
  //CAXX1419, Emsdale
  //CAXX1420, Enchant
  //CAXX1421, Englehart
  //CAXX1422, English+Harbour+East
  //CAXX1423, Ennadai
  //CAXX1426, Erieau
  //CAXX1427, Eriksdale
  //CAXX1428, Erin
  //CAXX1429, Espanola
  //CAXX1430, Estaire
  //CAXX1431, Esterel
  //CAXX1432, Esterhazy
  //CAXX1433, Eston
  //CAXX1434, Etzikom
  //CAXX1435, Eugenia
  //CAXX1437, Exeter
  //CAXX1438, Eyebrow
  //CAXX1439, Fabre
  //CAXX1440, Fabreville
  //CAXX1441, Farnham
  //CAXX1444, Faust
  //CAXX1445, Fenelon+Falls
  //CAXX1446, Fergus
  //CAXX1447, Ferintosh
  //CAXX1448, Ferme-Neuve
  //CAXX1449, Fermeuse
  //CAXX1452, Fillmore
  //CAXX1453, Finch
  //CAXX1454, Fingal
  //CAXX1455, Flanders
  //CAXX1456, Flatbush
  //CAXX1457, Fleming
  //CAXX1458, Flesherton
  //CAXX1459, Fleur+De+Lys
  //CAXX1460, Foam+Lake
  //CAXX1461, Fogo
  //CAXX1462, Foleyet
  //CAXX1463, Fond-Du-Lac
  //CAXX1464, Foremost
  //CAXX1465, Forest
  //CAXX1466, Forestburg
  //CAXX1467, Forestville
  //CAXX1468, Fort+Albany
  //CAXX1469, Fort+Assiniboine
  //CAXX1470, Fort+Fraser
  //CAXX1471, Fort+Good+Hope
  //CAXX1472, Fort+Hope
  //CAXX1473, Fort+Liard
  //CAXX1474, Fort+Macleod
  //CAXX1475, Fort+Providence
  //CAXX1476, Fort+Qu+Appelle
  //CAXX1477, Fort+Resolution
  //CAXX1478, Fort+Severn
  //CAXX1479, Fort+Thomson
  //CAXX1480, Fort+Vermilion
  //CAXX1481, Fort+Vermillion
  //CAXX1482, Fortierville
  //CAXX1483, Fortune
  //CAXX1484, Fox+Valley
  //CAXX1485, Foymount
  //CAXX1486, Francis
  //CAXX1487, Frankford
  //CAXX1488, Fraser+Lake
  //CAXX1489, Fredericton+Junction
  //CAXX1490, French+Village
  //CAXX1491, Freshwater
  //CAXX1492, Frobisher
  //CAXX1493, Fruitvale
  //CAXX1494, Gabarus
  //CAXX1495, Gabriola
  //CAXX1496, Gadsby
  //CAXX1497, Gagetown
  //CAXX1498, Gainsborough
  //CAXX1499, Galahad
  //CAXX1500, Galt
  //CAXX1501, Ganges
  //CAXX1502, Garden+River
  //CAXX1503, Garnish
  //CAXX1504, Garson
  //CAXX1505, Gaultois
  //CAXX1508, Gibsons
  //CAXX1509, Gilbert+Plains
  //CAXX1510, Gilmour
  //CAXX1511, Girouxville
  //CAXX1512, Gjoa+Haven
  //CAXX1513, Gladstone
  //CAXX1514, Glaslyn
  //CAXX1515, Glassville
  //CAXX1516, Gleichen
  //CAXX1517, Glen+Robertson
  //CAXX1518, Glenboro
  //CAXX1519, Glencoe
  //CAXX1520, Glendon
  //CAXX1522, Glovertown
  //CAXX1523, Gogama
  //CAXX1524, Gold+Bridge
  //CAXX1525, Goldboro
  //CAXX1526, Golden+Lake
  //CAXX1527, Gooderham
  //CAXX1528, Goodsoil
  //CAXX1529, Goshen
  //CAXX1530, Govan
  //CAXX1531, Gowganda
  //CAXX1532, Grand+Bank
  //CAXX1533, Grand+Beach
  //CAXX1534, Grand+Bend
  //CAXX1536, Grand+Forks
  //CAXX1537, Grand+Narrows
  //CAXX1538, Grand+Valley
  //CAXX1539, Grand-Sault
  //CAXX1540, Grande-Entree
  //CAXX1541, Grande-Riviere
  //CAXX1542, Grande-Vallee
  //CAXX1543, Grandes-Bergeronnes
  //CAXX1544, Grandes-Piles
  //CAXX1545, Grassland
  //CAXX1546, Grassy+Lake
  //CAXX1547, Grassy+Plains
  //CAXX1548, Grates+Cove
  //CAXX1549, Gravelbourg
  //CAXX1550, Great+Village
  //CAXX1551, Green+Lake
  //CAXX1552, Greenfield+Park
  //CAXX1553, Greenspond
  //CAXX1555, Grenfell
  //CAXX1556, Grenville
  //CAXX1557, Grimshaw
  //CAXX1558, Guigues
  //CAXX1559, Gull+Lake
  //CAXX1560, Guysborough
  //CAXX1561, Gypsumville
  //CAXX1562, Hafford
  //CAXX1563, Hagersville
  //CAXX1564, Halkirk
  //CAXX1565, Hamiota
  //CAXX1566, Hanley
  //CAXX1567, Hanover
  //CAXX1568, Hantsport
  //CAXX1569, Harbour+Breton
  //CAXX1570, Harbour+Grace
  //CAXX1571, Hardisty
  //CAXX1572, Harrington+Harbour
  //CAXX1573, Harriston
  //CAXX1574, Harrow
  //CAXX1575, Hartland
  //CAXX1576, Hartley+Bay
  //CAXX1577, Hartney
  //CAXX1578, Hastings
  //CAXX1581, Havre-Aubert
  //CAXX1582, Havre-Saint-Pierre
  //CAXX1583, Hawarden
  //CAXX1584, Hay+Lakes
  //CAXX1585, Hazelton
  //CAXX1586, Heart+s+Content
  //CAXX1587, Heart+s+Desire
  //CAXX1588, Hebertville
  //CAXX1589, Hedley
  //CAXX1590, Heinsburg
  //CAXX1591, Heisler
  //CAXX1592, Hemmingford
  //CAXX1593, Hensall
  //CAXX1594, Herbert
  //CAXX1595, Herschel
  //CAXX1596, Hespeler
  //CAXX1597, High+Prairie
  //CAXX1598, High+River
  //CAXX1599, Hilda
  //CAXX1600, Hillsborough
  //CAXX1601, Hines+Creek
  //CAXX1602, Hinton
  //CAXX1603, Hixon
  //CAXX1604, Hobbema
  //CAXX1605, Hodgeville
  //CAXX1606, Holberg
  //CAXX1607, Holden
  //CAXX1608, Holman
  //CAXX1609, Holyrood
  //CAXX1610, Hopewell
  //CAXX1611, Hornepayne
  //CAXX1612, Horsefly
  //CAXX1613, Houston
  //CAXX1614, Howick
  //CAXX1615, Hubbards
  //CAXX1618, Humboldt
  //CAXX1619, Hunter+River
  //CAXX1620, Huntingdon
  //CAXX1621, Hussar
  //CAXX1622, Hythe
  //CAXX1623, Iberville
  //CAXX1624, Ile-a-La-Crosse
  //CAXX1625, Indian+Head
  //CAXX1626, Ingleside
  //CAXX1629, Inwood
  //CAXX1630, Irma
  //CAXX1631, Iroquois
  //CAXX1632, Iroquois+Falls
  //CAXX1633, Irricana
  //CAXX1634, Island+Falls
  //CAXX1635, Island+Harbour
  //CAXX1636, Ituna
  //CAXX1637, Ivugivik
  //CAXX1638, Jacquet+River
  //CAXX1639, Jamestown
  //CAXX1640, Jansen
  //CAXX1641, Jarvie
  //CAXX1642, Jean+Marie+River
  //CAXX1643, Jenner
  //CAXX1644, Joggins
  //CAXX1645, Joussard
  //CAXX1646, Kakisa
  //CAXX1647, Kamsack
  //CAXX1648, Kedgwick
  //CAXX1649, Keg+River
  //CAXX1650, Kelliher
  //CAXX1651, Kelvington
  //CAXX1652, Kenaston
  //CAXX1653, Kennedy
  //CAXX1654, Kennetcook
  //CAXX1655, Kensington
  //CAXX1656, Keremeos
  //CAXX1657, Kerrobert
  //CAXX1659, Kiamika
  //CAXX1660, Killam
  //CAXX1662, Kincaid
  //CAXX1663, Kincolith
  //CAXX1664, King+City
  //CAXX1665, Kingsville
  //CAXX1666, Kinistino
  //CAXX1667, Kinmount
  //CAXX1668, Kipawa
  //CAXX1669, Kisbey
  //CAXX1670, Kitimat
  //CAXX1671, Kitwanga
  //CAXX1672, Klemtu
  //CAXX1673, Knowlton
  //CAXX1674, Kugaaruk
  //CAXX1675, Kugluktuk
  //CAXX1676, l+Annonciation
  //CAXX1677, l+Anse-Saint-Jean
  //CAXX1678, l+Assomption
  //CAXX1679, l+Epiphanie
  //CAXX1680, l+Orignal
  //CAXX1681, La+Guadeloupe
  //CAXX1682, La+Loche
  //CAXX1683, La+Reine
  //CAXX1684, La+Sarre
  //CAXX1685, La+Scie
  //CAXX1686, Labelle
  //CAXX1687, Lac+Du+Bonnet
  //CAXX1688, Lac+Seul
  //CAXX1689, Lac-Au-Saumon
  //CAXX1690, Lac-Aux-Sables
  //CAXX1691, Lac-Bouchette
  //CAXX1692, Lac-Megantic
  //CAXX1693, Lachute
  //CAXX1694, Lacolle
  //CAXX1695, Ladle+Cove
  //CAXX1696, Ladysmith
  //CAXX1697, Lafleche
  //CAXX1698, Lafontaine
  //CAXX1699, Laird
  //CAXX1700, Lake+Alma
  //CAXX1701, Lake+Cowichan
  //CAXX1702, Lake+Lenore
  //CAXX1703, Lamont
  //CAXX1704, Lampman
  //CAXX1705, Lancaster
  //CAXX1706, Landis
  //CAXX1707, Landrienne
  //CAXX1708, Lang
  //CAXX1709, Langenburg
  //CAXX1710, Langley
  //CAXX1711, Langruth
  //CAXX1712, Laniel
  //CAXX1713, Lanigan
  //CAXX1714, Lanoraie
  //CAXX1715, Larder+Lake
  //CAXX1716, Lark+Harbour
  //CAXX1717, Larrys+River
  //CAXX1718, Latchford
  //CAXX1719, Laurentides
  //CAXX1720, Laval-Des-Rapides
  //CAXX1721, Laval-Ouest
  //CAXX1722, Lavaltrie
  //CAXX1723, Laverlochere
  //CAXX1724, Lavoy
  //CAXX1725, Leduc
  //CAXX1726, Lefroy
  //CAXX1727, Legal
  //CAXX1728, Lemberg
  //CAXX1729, Leoville
  //CAXX1730, Lery
  //CAXX1731, Les+Escoumins
  //CAXX1732, Les+Mechins
  //CAXX1733, Leslieville
  //CAXX1734, Levack
  //CAXX1735, Lewisporte
  //CAXX1736, Lillooet
  //CAXX1737, Limerick
  //CAXX1738, Lintlaw
  //CAXX1739, Lipton
  //CAXX1740, Listowel
  //CAXX1741, Little+Burnt+Bay
  //CAXX1742, Little+Current
  //CAXX1743, Little+Fort
  //CAXX1744, Little+Grand+Rapids
  //CAXX1745, Liverpool
  //CAXX1747, Lockeport
  //CAXX1749, Long+Sault
  //CAXX1750, Loon+Lake
  //CAXX1751, Loos
  //CAXX1752, Lotbiniere
  //CAXX1753, Lougheed
  //CAXX1754, Louisbourg
  //CAXX1755, Louiseville
  //CAXX1756, Lourdes
  //CAXX1757, Low
  //CAXX1758, Lower+Island+Cove
  //CAXX1759, Lower+Post
  //CAXX1760, Lucknow
  //CAXX1761, Lucky+Lake
  //CAXX1762, Lumby
  //CAXX1764, Luseland
  //CAXX1765, Luskville
  //CAXX1766, Lyster
  //CAXX1767, Mabou
  //CAXX1768, Macamic
  //CAXX1769, Macdiarmid
  //CAXX1770, Macgregor
  //CAXX1771, Macklin
  //CAXX1772, Mactier
  //CAXX1773, Madoc
  //CAXX1774, Madsen
  //CAXX1775, Mafeking
  //CAXX1776, Magnetawan
  //CAXX1777, Mahone+Bay
  //CAXX1778, Maidstone
  //CAXX1779, Makkovik
  //CAXX1780, Malahat
  //CAXX1781, Manigotagan
  //CAXX1782, Manitou
  //CAXX1783, Manitouwadge
  //CAXX1784, Manitowaning
  //CAXX1785, Mankota
  //CAXX1786, Mannville
  //CAXX1787, Manor
  //CAXX1788, Manouane
  //CAXX1789, Manseau
  //CAXX1790, Manyberries
  //CAXX1791, Maple+Grove
  //CAXX1792, Marcelin
  //CAXX1793, Marieville
  //CAXX1794, Markdale
  //CAXX1795, Markstay
  //CAXX1796, Marmora
  //CAXX1797, Marwayne
  //CAXX1798, Maryfield
  //CAXX1799, Marystown
  //CAXX1800, Maskinonge
  //CAXX1801, Masset
  //CAXX1802, Matachewan
  //CAXX1803, Matheson
  //CAXX1804, Mattawa
  //CAXX1805, Maxville
  //CAXX1806, Maymont
  //CAXX1807, Maynooth
  //CAXX1808, Mcbride
  //CAXX1809, Mccreary
  //CAXX1810, Mclennan
  //CAXX1811, Mcleod+Lake
  //CAXX1812, Mcmasterville
  //CAXX1813, Meacham
  //CAXX1814, Meander+River
  //CAXX1815, Meath+Park
  //CAXX1816, Meductic
  //CAXX1817, Melocheville
  //CAXX1818, Melville
  //CAXX1819, Merigomish
  //CAXX1820, Merrickville
  //CAXX1821, Metabetchouan
  //CAXX1822, Meteghan
  //CAXX1823, Midway
  //CAXX1824, Milden
  //CAXX1825, Mildmay
  //CAXX1826, Millbrook
  //CAXX1827, Millertown
  //CAXX1828, Millet
  //CAXX1829, Millhaven
  //CAXX1830, Millville
  //CAXX1831, Milo
  //CAXX1832, Milverton
  //CAXX1833, Minden
  //CAXX1834, Miniota
  //CAXX1835, Minto
  //CAXX1836, Minton
  //CAXX1837, Missanabie
  //CAXX1838, Mistatim
  //CAXX1839, Mitchell
  //CAXX1840, Mont-Louis
  //CAXX1841, Mont-Royal
  //CAXX1842, Mont-Saint-Hilaire
  //CAXX1843, Montague
  //CAXX1844, Montebello
  //CAXX1845, Montmagny
  //CAXX1846, Montmartre
  //CAXX1847, Montney
  //CAXX1848, Montreal-Est
  //CAXX1849, Montreal-Nord
  //CAXX1850, Montreal-Ouest
  //CAXX1851, Moosomin
  //CAXX1852, Morell
  //CAXX1853, Morley
  //CAXX1854, Morrin
  //CAXX1855, Morris
  //CAXX1856, Morrisburg
  //CAXX1857, Morson
  //CAXX1858, Mortlach
  //CAXX1859, Mossbank
  //CAXX1860, Mount+Pleasant
  //CAXX1861, Mount+Uniacke
  //CAXX1862, Moyie
  //CAXX1863, Mulgrave
  //CAXX1864, Murdochville
  //CAXX1865, Muskoka+Falls
  //CAXX1866, Musquodoboit+Harbour
  //CAXX1867, Mutton+Bay
  //CAXX1868, Myrnam
  //CAXX1869, Nahanni+Butte
  //CAXX1870, Naicam
  //CAXX1871, Nain
  //CAXX1872, Nakina
  //CAXX1873, Namao
  //CAXX1874, Nampa
  //CAXX1875, Nanton
  //CAXX1876, Napanee
  //CAXX1877, Napierville
  //CAXX1878, Neepawa
  //CAXX1879, Neguac
  //CAXX1880, Neidpath
  //CAXX1881, Neilburg
  //CAXX1882, Nemiscau
  //CAXX1883, Neudorf
  //CAXX1884, Neuville
  //CAXX1885, New+Dayton
  //CAXX1886, New+Denver
  //CAXX1887, New+Germany
  //CAXX1888, New+Hamburg
  //CAXX1889, New+Harbour
  //CAXX1890, New+Liskeard
  //CAXX1891, New+Perlican
  //CAXX1892, New+Richmond
  //CAXX1893, New+Ross
  //CAXX1894, Newbrook
  //CAXX1895, Newburgh
  //CAXX1897, Newdale
  //CAXX1898, Newport
  //CAXX1899, Niagara-On-The-Lake
  //CAXX1900, Nipawin
  //CAXX1901, Nippers+Harbour
  //CAXX1902, Nisku
  //CAXX1903, Nobleford
  //CAXX1904, Noelville
  //CAXX1905, Nokomis
  //CAXX1906, Nordegg
  //CAXX1907, Normandin
  //CAXX1908, Normetal
  //CAXX1909, Norris+Arm
  //CAXX1910, North+Portal
  //CAXX1911, Norwich
  //CAXX1912, Norwood
  //CAXX1913, Notre-Dame-Du-Lac
  //CAXX1914, Notre-Dame-Du-Laus
  //CAXX1915, Notre-Dame-Du-Nord
  //CAXX1916, Nouvelle
  //CAXX1917, o+Leary
  //CAXX1918, Oak+Lake
  //CAXX1919, Oba
  //CAXX1920, Ocean+Falls
  //CAXX1921, Ochre+River
  //CAXX1922, Ogema
  //CAXX1924, Oka
  //CAXX1925, Old+Perlican
  //CAXX1926, Olds
  //CAXX1927, Omerville
  //CAXX1928, Onefour
  //CAXX1929, Onoway
  //CAXX1930, Orleans
  //CAXX1931, Ormstown
  //CAXX1932, Orrville
  //CAXX1933, Otterburn+Park
  //CAXX1934, Outlook
  //CAXX1935, Oxbow
  //CAXX1936, Oxdrift
  //CAXX1937, Oxford
  //CAXX1938, Oxford+House
  //CAXX1939, Packs+Harbour
  //CAXX1940, Pacquet
  //CAXX1941, Paddockwood
  //CAXX1942, Paisley
  //CAXX1943, Pakenham
  //CAXX1944, Palmarolle
  //CAXX1945, Palmerston
  //CAXX1946, Paradise
  //CAXX1947, Paradise+Hill
  //CAXX1948, Paradise+Valley
  //CAXX1949, Parham
  //CAXX1950, Paris
  //CAXX1951, Parkhill
  //CAXX1952, Parrsboro
  //CAXX1953, Pass+Lake
  //CAXX1954, Paynton
  //CAXX1955, Pefferlaw
  //CAXX1956, Pelican+Narrows
  //CAXX1957, Pelican+Rapids
  //CAXX1958, Pelly
  //CAXX1959, Pelly+Crossing
  //CAXX1960, Penetanguishene
  //CAXX1961, Penhold
  //CAXX1962, Perdue
  //CAXX1963, Perth
  //CAXX1964, Petitcodiac
  //CAXX1965, Petrolia
  //CAXX1966, Piapot
  //CAXX1967, Pictou
  //CAXX1968, Pierceland
  //CAXX1969, Pierrefonds
  //CAXX1970, Pierreville
  //CAXX1971, Pikangikum
  //CAXX1972, Pikwitonei
  //CAXX1973, Pinawa
  //CAXX1974, Pincourt
  //CAXX1975, Pine+Falls
  //CAXX1976, Pine+River
  //CAXX1977, Piney
  //CAXX1978, Pitt+Meadows
  //CAXX1979, Placentia
  //CAXX1980, Plamondon
  //CAXX1981, Plaster+Rock
  //CAXX1982, Plessisville
  //CAXX1983, Plum+Coulee
  //CAXX1984, Plumas
  //CAXX1985, Point+Leamington
  //CAXX1986, Pointe+Du+Bois
  //CAXX1987, Pointe-Au-Pere
  //CAXX1988, Pointe-Aux-Trembles
  //CAXX1989, Pointe-Calumet
  //CAXX1990, Pointe-Claire
  //CAXX1991, Pond+Inlet
  //CAXX1992, Ponoka
  //CAXX1993, Pont-Rouge
  //CAXX1994, Pont-Viau
  //CAXX1995, Ponteix
  //CAXX1996, Porcupine+Plain
  //CAXX1997, Port+Alice
  //CAXX1998, Port+Au+Choix
  //CAXX1999, Port+Blandford
  //CAXX2000, Port+Burwell
  //CAXX2001, Port+Clements
  //CAXX2002, Port+Credit
  //CAXX2003, Port+Dufferin
  //CAXX2005, Port+Greville
  //CAXX2006, Port+Hood
  //CAXX2007, Port+Hope
  //CAXX2008, Port+Hope+Simpson
  //CAXX2009, Port+Loring
  //CAXX2010, Port+Maitland
  //CAXX2011, Port+Mcnicoll
  //CAXX2012, Port+Mellon
  //CAXX2013, Port+Morien
  //CAXX2014, Port+Mouton
  //CAXX2015, Port+Perry
  //CAXX2016, Port+Renfrew
  //CAXX2017, Port+Rexton
  //CAXX2018, Port+Rowan
  //CAXX2019, Port+Saunders
  //CAXX2020, Port+Severn
  //CAXX2021, Port+Stanley
  //CAXX2022, Port+Union
  //CAXX2023, Port-Daniel
  //CAXX2024, Pouce+Coupe
  //CAXX2025, Poularies
  //CAXX2026, Preeceville
  //CAXX2027, Prescott
  //CAXX2029, Princeville
  //CAXX2030, Provost
  //CAXX2031, Pubnico
  //CAXX2032, Pugwash
  //CAXX2033, Puvirnituq
  //CAXX2034, Qu+Appelle
  //CAXX2035, Quathiaski+Cove
  //CAXX2036, Queensport
  //CAXX2037, Queenston
  //CAXX2038, Quill+Lake
  //CAXX2039, Quyon
  //CAXX2040, Rabbit+Lake
  //CAXX2042, Radville
  //CAXX2043, Radway
  //CAXX2044, Raith
  //CAXX2045, Ramea
  //CAXX2046, Ramore
  //CAXX2047, Rapid+City
  //CAXX2048, Rawdon
  //CAXX2049, Raymore
  //CAXX2050, Red+Bay
  //CAXX2052, Redvers
  //CAXX2053, Rencontre+East
  //CAXX2054, Renfrew
  //CAXX2055, Rennie
  //CAXX2056, Repentigny
  //CAXX2058, Reston
  //CAXX2059, Riceton
  //CAXX2060, Rich+Lake
  //CAXX2061, Richelieu
  //CAXX2062, Richibucto
  //CAXX2065, Ridgetown
  //CAXX2066, Rigaud
  //CAXX2067, Rigolet
  //CAXX2068, Rimbey
  //CAXX2069, Riondel
  //CAXX2070, Ripley
  //CAXX2071, Ripon
  //CAXX2072, Riske+Creek
  //CAXX2073, River+Hebert
  //CAXX2074, River+John
  //CAXX2075, River+Of+Ponds
  //CAXX2076, Riverhurst
  //CAXX2077, Rivers
  //CAXX2078, Riviere-a-Pierre
  //CAXX2079, Riviere-Au-Renard
  //CAXX2080, Riviere-Au-Tonnerre
  //CAXX2081, Riviere-Bleue
  //CAXX2082, Riviere-Saint-Jean
  //CAXX2083, Robertsonville
  //CAXX2084, Roblin
  //CAXX2085, Rocanville
  //CAXX2086, Rochebaucourt
  //CAXX2087, Rock+Creek
  //CAXX2088, Rockland
  //CAXX2089, Roddickton
  //CAXX2090, Rodney
  //CAXX2091, Rogersville
  //CAXX2092, Roland
  //CAXX2093, Rollet
  //CAXX2094, Rorketon
  //CAXX2095, Rosalind
  //CAXX2096, Rosebud
  //CAXX2097, Rosemere
  //CAXX2098, Ross+River
  //CAXX2099, Rossburn
  //CAXX2100, Rosthern
  //CAXX2101, Rouleau
  //CAXX2102, Roxboro
  //CAXX2103, Rumsey
  //CAXX2104, Russell
  //CAXX2105, Rycroft
  //CAXX2106, Sachs+Harbour
  //CAXX2107, Sacre-Coeur
  //CAXX2109, Saint+Bride+s
  //CAXX2110, Saint+Brieux
  //CAXX2111, Saint+Clair+Beach
  //CAXX2112, Saint+George
  //CAXX2113, Saint+George+s
  //CAXX2114, Saint+Gregor
  //CAXX2115, Saint+Martins
  //CAXX2116, Saint+Paul
  //CAXX2117, Saint+Peters+Bay
  //CAXX2119, Saint+Thomas
  //CAXX2120, Saint+Walburg
  //CAXX2121, Saint-Agapit
  //CAXX2122, Saint-Alexandre
  //CAXX2123, Saint-Alexandre-De-Kamouraska
  //CAXX2124, Saint-Ambroise
  //CAXX2125, Saint-Andre
  //CAXX2126, Saint-Andre-Avellin
  //CAXX2127, Saint-Anicet
  //CAXX2128, Saint-Anselme
  //CAXX2129, Saint-Antoine-Des-Laurentides
  //CAXX2130, Saint-Antonin
  //CAXX2131, Saint-Athanase
  //CAXX2132, Saint-Barthelemy
  //CAXX2133, Saint-Basile
  //CAXX2134, Saint-Bruno-De-Guigues
  //CAXX2135, Saint-Bruno-De-Montarville
  //CAXX2136, Saint-Casimir
  //CAXX2137, Saint-Cesaire
  //CAXX2138, Saint-Charles-De-Bellechasse
  //CAXX2139, Saint-Charles-Sur-Richelieu
  //CAXX2140, Saint-Chrysostome
  //CAXX2141, Saint-Eleuthere
  //CAXX2142, Saint-Elie-d+Orford
  //CAXX2143, Saint-Fabien
  //CAXX2144, Saint-Felix-De-Valois
  //CAXX2145, Saint-Francois-Du-Lac
  //CAXX2146, Saint-Fulgence
  //CAXX2147, Saint-Gabriel-De-Brandon
  //CAXX2148, Saint-Gedeon
  //CAXX2149, Saint-Gedeon-De-Beauce
  //CAXX2150, Saint-Georges-De-Cacouna
  //CAXX2151, Saint-Gerard
  //CAXX2152, Saint-Germain-De-Grantham
  //CAXX2153, Saint-Henri
  //CAXX2154, Saint-Honore-De-Temiscouata
  //CAXX2155, Saint-Jean-Baptiste
  //CAXX2156, Saint-Jean-De-Dieu
  //CAXX2157, Saint-Jean-De-Matha
  //CAXX2158, Saint-Jean-Port-Joli
  //CAXX2159, Saint-Jerome
  //CAXX2160, Saint-Joachim
  //CAXX2161, Saint-Joseph-De-Beauce
  //CAXX2162, Saint-Joseph-De-Sorel
  //CAXX2163, Saint-Jude
  //CAXX2164, Saint-Lambert
  //CAXX2165, Saint-Laurent
  //CAXX2166, Saint-Lazare
  //CAXX2167, Saint-Leon-De-Standon
  //CAXX2168, Saint-Leonard
  //CAXX2169, Saint-Lin
  //CAXX2170, Saint-Ludger
  //CAXX2171, Saint-Malachie
  //CAXX2172, Saint-Malo
  //CAXX2173, Saint-Marc-Des-Carrieres
  //CAXX2174, Saint-Michel-Des-Saints
  //CAXX2175, Saint-Moise
  //CAXX2176, Saint-Nicolas
  //CAXX2177, Saint-Noel
  //CAXX2178, Saint-Ours
  //CAXX2179, Saint-Pacome
  //CAXX2180, Saint-Pamphile
  //CAXX2181, Saint-Pascal
  //CAXX2182, Saint-Paul-De-Montminy
  //CAXX2183, Saint-Philippe-De-Neri
  //CAXX2184, Saint-Pie
  //CAXX2185, Saint-Pierre
  //CAXX2186, Saint-Prosper-De-Dorchester
  //CAXX2187, Saint-Raphael
  //CAXX2188, Saint-Raymond
  //CAXX2189, Saint-Remi
  //CAXX2190, Saint-Roch-Des-Aulnaies
  //CAXX2191, Saint-Romuald
  //CAXX2192, Saint-Sauveur-Des-Monts
  //CAXX2193, Saint-Simon-De-Rimouski
  //CAXX2194, Saint-Stanislas
  //CAXX2195, Saint-Sylvestre
  //CAXX2196, Saint-Timothee
  //CAXX2197, Saint-Tite
  //CAXX2198, Saint-Tite-Des-Caps
  //CAXX2199, Saint-Vincent-De-Paul
  //CAXX2200, Saint-Zacharie
  //CAXX2201, Saint-Zotique
  //CAXX2202, Sainte-Adele
  //CAXX2203, Sainte-Agathe
  //CAXX2204, Sainte-Agathe-Des-Monts
  //CAXX2205, Sainte-Anne-De-Beaupre
  //CAXX2206, Sainte-Anne-De-Bellevue
  //CAXX2207, Sainte-Anne-Des-Monts
  //CAXX2208, Sainte-Anne-Du-Lac
  //CAXX2209, Sainte-Blandine
  //CAXX2210, Sainte-Cecile-De-Masham
  //CAXX2211, Sainte-Claire
  //CAXX2212, Sainte-Croix
  //CAXX2213, Sainte-Dorothee
  //CAXX2214, Sainte-Famille
  //CAXX2215, Sainte-Henedine
  //CAXX2216, Sainte-Julienne
  //CAXX2217, Sainte-Marguerite
  //CAXX2218, Sainte-Marie
  //CAXX2219, Sainte-Martine
  //CAXX2220, Sainte-Monique
  //CAXX2221, Sainte-Rosalie
  //CAXX2222, Sainte-Rose
  //CAXX2223, Sainte-Thecle
  //CAXX2224, Sainte-Therese
  //CAXX2225, Salaberry-De-Valleyfield
  //CAXX2226, Salisbury
  //CAXX2227, Salmo
  //CAXX2228, Salmon+Cove
  //CAXX2229, Salvage
  //CAXX2230, Sandwich
  //CAXX2231, Sandy+Lake
  //CAXX2232, Sangudo
  //CAXX2233, Savant+Lake
  //CAXX2234, Sayabec
  //CAXX2235, Sayward
  //CAXX2236, Schreiber
  //CAXX2237, Scotstown
  //CAXX2238, Seaforth
  //CAXX2239, Searchmont
  //CAXX2240, Seba+Beach
  //CAXX2241, Sechelt
  //CAXX2242, Semans
  //CAXX2243, Senneterre
  //CAXX2244, Senneville
  //CAXX2245, Severn+Bridge
  //CAXX2246, Shalalth
  //CAXX2248, Sharbot+Lake
  //CAXX2249, Shaunavon
  //CAXX2250, Shawanaga
  //CAXX2251, Shawbridge
  //CAXX2252, Shawinigan-Sud
  //CAXX2253, Shawville
  //CAXX2254, Shebandowan
  //CAXX2255, Shefford
  //CAXX2256, Sheho
  //CAXX2259, Shell+Lake
  //CAXX2260, Shellbrook
  //CAXX2262, Sherwood+Park
  //CAXX2263, Shingle+Point
  //CAXX2264, Shipshaw
  //CAXX2265, Shoal+Lake
  //CAXX2266, Shubenacadie
  //CAXX2267, Sicamous
  //CAXX2269, Simpson
  //CAXX2270, Sintaluta
  //CAXX2271, Skookumchuck
  //CAXX2272, Slocan
  //CAXX2273, Smith
  //CAXX2274, Smithville
  //CAXX2275, Smoky+Lake
  //CAXX2276, Smooth+Rock+Falls
  //CAXX2277, Snag
  //CAXX2278, Sointula
  //CAXX2279, Sombra
  //CAXX2280, Sooke
  //CAXX2282, South+Brook
  //CAXX2283, South+Indian+Lake
  //CAXX2284, South+River
  //CAXX2285, Southampton
  //CAXX2286, Southey
  //CAXX2287, Sparta
  //CAXX2288, Spences+Bridge
  //CAXX2289, Sperling
  //CAXX2290, Spillimacheen
  //CAXX2291, Spirit+River
  //CAXX2292, Split+Lake
  //CAXX2293, Sprague
  //CAXX2294, Springdale
  //CAXX2295, Springfield
  //CAXX2296, Sprucedale
  //CAXX2297, Spy+Hill
  //CAXX2298, Squamish
  //CAXX2299, Standard
  //CAXX2300, Stanley
  //CAXX2301, Stanstead
  //CAXX2302, Star+City
  //CAXX2303, Steady+Brook
  //CAXX2304, Steep+Rock
  //CAXX2305, Steinbach
  //CAXX2306, Stellarton
  //CAXX2307, Stephenville+Crossing
  //CAXX2308, Stettler
  //CAXX2309, Stewart
  //CAXX2310, Stewiacke
  //CAXX2313, Stony+Rapids
  //CAXX2314, Stouffville
  //CAXX2315, Stoughton
  //CAXX2316, Strasbourg
  //CAXX2317, Stratford
  //CAXX2318, Strathclair
  //CAXX2319, Streetsville
  //CAXX2320, Sturdies+Bay
  //CAXX2321, Sturgis
  //CAXX2322, Sultan
  //CAXX2323, Summerford
  //CAXX2324, Summit+Lake
  //CAXX2325, Sundridge
  //CAXX2326, Sunnyside
  //CAXX2327, Sussex
  //CAXX2329, Swan+Lake
  //CAXX2330, Swastika
  //CAXX2331, Sydenham
  //CAXX2332, Sydney+River
  //CAXX2333, Tachie
  //CAXX2334, Tadoussac
  //CAXX2335, Tagish
  //CAXX2336, Tahsis
  //CAXX2337, Taloyoak
  //CAXX2338, Tamworth
  //CAXX2339, Tangier
  //CAXX2340, Taschereau
  //CAXX2341, Tatamagouche
  //CAXX2342, Tatlayoko+Lake
  //CAXX2343, Tavistock
  //CAXX2344, Teeswater
  //CAXX2345, Telegraph+Creek
  //CAXX2346, Temiscaming
  //CAXX2347, Terra+Nova
  //CAXX2348, Terrace+Bay
  //CAXX2349, Terrebonne
  //CAXX2350, Terrenceville
  //CAXX2351, Tete-a-La-Baleine
  //CAXX2352, Teulon
  //CAXX2353, Thamesville
  //CAXX2354, Thedford
  //CAXX2355, Theodore
  //CAXX2356, Thessalon
  //CAXX2357, Thetford+Mines
  //CAXX2358, Thicket+Portage
  //CAXX2359, Thorhild
  //CAXX2360, Thornhill
  //CAXX2361, Thurso
  //CAXX2362, Tignish
  //CAXX2363, Tilbury
  //CAXX2364, Tilley
  //CAXX2365, Tobermory
  //CAXX2366, Tofield
  //CAXX2367, Tompkins
  //CAXX2368, Topley
  //CAXX2369, Torrington
  //CAXX2370, Tottenham
  //CAXX2371, Tourville
  //CAXX2372, Tramping+Lake
  //CAXX2373, Treherne
  //CAXX2375, Trepassey
  //CAXX2376, Tribune
  //CAXX2377, Tring-Jonction
  //CAXX2378, Trochu
  //CAXX2379, Trois-Pistoles
  //CAXX2380, Trout+Creek
  //CAXX2383, Trout+River
  //CAXX2384, Tsiigehtchic
  //CAXX2385, Turner+Valley
  //CAXX2386, Turtleford
  //CAXX2387, Tweed
  //CAXX2388, Two+Hills
  //CAXX2389, Tyne+Valley
  //CAXX2390, Unity
  //CAXX2391, Upper+Island+Cove
  //CAXX2392, Upper+Musquodoboit
  //CAXX2393, Utterson
  //CAXX2394, Uxbridge
  //CAXX2395, Val+Marie
  //CAXX2396, Val-Barrette
  //CAXX2397, Val-Brillant
  //CAXX2398, Val-David
  //CAXX2399, Valcourt
  //CAXX2400, Valemount
  //CAXX2401, Vallee-Jonction
  //CAXX2402, Valleyview
  //CAXX2403, Vallican
  //CAXX2404, Vanderhoof
  //CAXX2405, Vanguard
  //CAXX2406, Vankleek+Hill
  //CAXX2407, Vanscoy
  //CAXX2408, Varennes
  //CAXX2409, Vauxhall
  //CAXX2410, Vercheres
  //CAXX2411, Vermilion+Bay
  //CAXX2412, Verner
  //CAXX2413, Vernon
  //CAXX2414, Vernon+River
  //CAXX2415, Verona
  //CAXX2417, Victoriaville
  //CAXX2418, Viking
  //CAXX2419, Villemontel
  //CAXX2420, Vilna
  //CAXX2421, Viscount
  //CAXX2422, Vonda
  //CAXX2423, Vulcan
  //CAXX2424, Wabana
  //CAXX2425, Wabigoon
  //CAXX2426, Wabowden
  //CAXX2427, Wadena
  //CAXX2428, Wainwright
  //CAXX2429, Wakaw
  //CAXX2430, Wakefield
  //CAXX2431, Waldheim
  //CAXX2432, Wallace
  //CAXX2433, Wallaceburg
  //CAXX2434, Walsh
  //CAXX2435, Walton
  //CAXX2436, Wandering+River
  //CAXX2437, Wapella
  //CAXX2438, Warner
  //CAXX2439, Warren
  //CAXX2440, Warspite
  //CAXX2441, Warwick
  //CAXX2442, Waskatenau
  //CAXX2443, Waskesiu+Lake
  //CAXX2444, Waswanipi
  //CAXX2445, Waterdown
  //CAXX2446, Waterford
  //CAXX2448, Waterville
  //CAXX2449, Watford
  //CAXX2450, Watson
  //CAXX2451, Waubaushene
  //CAXX2452, Wawanesa
  //CAXX2453, Wawota
  //CAXX2454, Webbwood
  //CAXX2455, Weedon
  //CAXX2458, Welsford
  //CAXX2459, Wesleyville
  //CAXX2460, West+Lorne
  //CAXX2461, Western+Bay
  //CAXX2462, Westlock
  //CAXX2463, Westmount
  //CAXX2466, Westree
  //CAXX2467, Westville
  //CAXX2468, Wetaskiwin
  //CAXX2469, Weymouth
  //CAXX2470, Wheatley
  //CAXX2471, Whitbourne
  //CAXX2472, Whitchurch-Stouffville
  //CAXX2473, White+Fox
  //CAXX2474, White+River
  //CAXX2475, White+Rock
  //CAXX2476, Whitefish
  //CAXX2477, Whitefish+Falls
  //CAXX2478, Whitelaw
  //CAXX2479, Whitemouth
  //CAXX2480, Whitewood
  //CAXX2481, Whitney
  //CAXX2482, Whycocomagh
  //CAXX2483, Widewater
  //CAXX2484, Wilberforce
  //CAXX2485, Wildwood
  //CAXX2486, Wilkie
  //CAXX2487, Willingdon
  //CAXX2488, Willow+Bunch
  //CAXX2491, Winfield
  //CAXX2492, Wingham
  //CAXX2493, Winkler
  //CAXX2494, Winnipegosis
  //CAXX2495, Winter+Harbour
  //CAXX2496, Winterton
  //CAXX2497, Wiseton
  //CAXX2498, Woburn
  //CAXX2499, Woking
  //CAXX2500, Wolfville
  //CAXX2501, Wolseley
  //CAXX2502, Wood+Mountain
  //CAXX2503, Woodridge
  //CAXX2504, Woody+Point
  //CAXX2505, Worsley
  //CAXX2506, Wrentham
  //CAXX2507, Wrigley
  //CAXX2508, Wyoming
  //CAXX2509, Yahk
  //CAXX2510, Yamachiche
  //CAXX2511, Yamaska
  //CAXX2512, Yarker
  //CAXX2513, Yellow+Grass
  //CAXX2514, Youbou
  //CAXX2515, Young
  //CAXX2516, Youngstown
  //CAXX2517, Zealandia
  //CAXX2518, Zeballos
  //CAXX2519, Cleardale
  //CAXX2521, Iron+Springs
  //CAXX2523, George+Island
  //CAXX2524, Caribou+Island
  //CAXX2525, Toronto+Islands
  //CAXX2526, Bonnard
  //CAXX2527, Onatchiway
  //CAXX2528, Pointe+Noire
  //CAXX2529, Saint+Paul+Island
  //CAXX2530, Tuktut+Nogait
  //CAXX2531, Cape+Saint+James
  //CAXX2532, Namaka
  //CAXX2533, Strathmore
  //CAXX2534, Pollockville
  //CAXX2536, Manning
  //CAXX2537, Notikewin
  //CAXX2538, Hotchkiss
  //CAXX2539, Picture+Butte
  //CAXX2540, Pointe-Des-Monts
  //CAXX2541, Baie-Trinite
  //CAXX2544, Purple+Springs
  //CAXX2545, Burdett
  //CAXX2546, Gallix
  //CAXX2547, Maliotenam
  //CAXX2548, Vaughan
  //CAXX2549, Lambton+Shores
  //CAXX2551, Haldimand
  //CAXX2552, Mascouche
  //CAXX2553, Sainte-Julie
  //CAXX2554, Saint-Constant
  //CAXX2555, Saint-Jean-Sur-Richelieu
  //CAXX2556, Delta
  //CAXX2557, Maple+Ridge
  //CAXX2558, Aldergrove
  //This page was last updated: 21st July 2011

////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////
      
      public Type getType() { return TYPE; }
      public static final Type TYPE = Sys.loadType(BLocationStorage.class);
   

}
